import uuid
from datetime import datetime, timezone
from typing import Optional

from sqlalchemy.orm import Session

from app.config import AppConfig
from modules.documents.models import DocumentChunk, DocumentMetadata
from modules.documents.repository import DocumentRepository
from modules.documents.status import DocumentStatus
from modules.embedding.events import EmbeddingEventPublisher, EmbeddingRequestedEvent
from modules.processing.chunkers import ChunkerRegistry
from modules.processing.parsers import DocumentParserRegistry
from modules.processing.retry_policy import ExponentialBackoffRetryPolicy
from modules.processing.source_fetchers import SourceFetcherRegistry


class ProcessingService:
    def __init__(
        self,
        db: Session,
        config: AppConfig,
        embedding_publisher: EmbeddingEventPublisher,
        retry_policy: Optional[ExponentialBackoffRetryPolicy] = None,
        source_fetchers: Optional[SourceFetcherRegistry] = None,
        parsers: Optional[DocumentParserRegistry] = None,
        chunkers: Optional[ChunkerRegistry] = None,
    ) -> None:
        self.db = db
        self.config = config
        self.embedding_publisher = embedding_publisher
        self.retry_policy = retry_policy or ExponentialBackoffRetryPolicy()
        self.documents = DocumentRepository(db)
        self.source_fetchers = source_fetchers or SourceFetcherRegistry()
        self.parsers = parsers or DocumentParserRegistry()
        self.chunkers = chunkers or ChunkerRegistry(
            chunk_size=config.processing.chunk_size,
            chunk_overlap=config.processing.chunk_overlap,
        )

    def process_document(self, document_id: uuid.UUID) -> int:
        document = self.documents.get(document_id)
        if document is None:
            raise ValueError(f"Document not found: {document_id}")

        try:
            self._mark_in_progress(document)
            raw_document = self.source_fetchers.get(document.source_type).fetch(document)
            parsed_document = self.parsers.get(document.file_format).parse(raw_document)
            chunks = self.chunkers.get(self.config.processing.chunking_strategy).chunk(
                parsed_document
            )

            self.documents.delete_chunks_for_document(document.id)
            for index, chunk in enumerate(chunks):
                self.documents.add_chunk(
                    DocumentChunk(
                        document_id=document.id,
                        content=chunk.content,
                        chunk_index=index,
                        chunk_metadata=chunk.metadata,
                    )
                )

            document.status = DocumentStatus.PROCESSED_TO_CHUNKS.value
            document.next_processing_attempt_at = None
            document.last_processing_error = None
            document.updated_at = datetime.now(timezone.utc)
            self.db.commit()

            self.embedding_publisher.publish(
                EmbeddingRequestedEvent(
                    document_id=document.id,
                    chunk_count=len(chunks),
                    embedding_model=self.config.processing.embedding_model,
                )
            )
            document.status = DocumentStatus.EMBEDDING_REQUESTED.value
            document.updated_at = datetime.now(timezone.utc)
            self.db.commit()
            return len(chunks)
        except Exception as exc:
            self.db.rollback()
            self._mark_failed_or_retry(document, exc)
            self.db.commit()
            raise

    def _mark_in_progress(self, document: DocumentMetadata) -> None:
        document.status = DocumentStatus.PROCESSING_IN_PROGRESS.value
        document.updated_at = datetime.now(timezone.utc)
        self.db.commit()

    def _mark_failed_or_retry(self, document: DocumentMetadata, exc: Exception) -> None:
        document.processing_attempts += 1
        document.last_processing_error = str(exc)
        document.updated_at = datetime.now(timezone.utc)

        if document.processing_attempts >= document.max_processing_attempts:
            document.status = DocumentStatus.FAILED.value
            document.next_processing_attempt_at = None
            return

        document.status = DocumentStatus.CREATED.value
        document.next_processing_attempt_at = self.retry_policy.next_attempt_at(
            document.processing_attempts
        )
