import uuid
from datetime import datetime, timezone
from typing import Optional

from sqlalchemy.orm import Session

from modules.documents.models import DocumentMetadata, Embedding
from modules.documents.repository import DocumentRepository
from modules.documents.status import DocumentStatus
from modules.embedding.providers import DeterministicEmbeddingProvider, EmbeddingProvider
from modules.processing.retry_policy import ExponentialBackoffRetryPolicy


class EmbeddingService:
    def __init__(
        self,
        db: Session,
        provider: Optional[EmbeddingProvider] = None,
        retry_policy: Optional[ExponentialBackoffRetryPolicy] = None,
    ) -> None:
        self.db = db
        self.provider = provider or DeterministicEmbeddingProvider()
        self.retry_policy = retry_policy or ExponentialBackoffRetryPolicy()
        self.documents = DocumentRepository(db)

    def embed_document(self, document_id: uuid.UUID) -> int:
        document = self.documents.get(document_id)
        if document is None:
            raise ValueError(f"Document not found: {document_id}")

        try:
            self._mark_in_progress(document)
            chunks = self.documents.list_chunks_for_document(document.id)
            vectors = self.provider.embed_texts([chunk.content for chunk in chunks])

            self.documents.delete_embeddings_for_document(document.id, self.provider.model)
            for chunk, vector in zip(chunks, vectors):
                self.documents.add_embedding(
                    Embedding(
                        chunk_id=chunk.id,
                        embedding=vector,
                        model=self.provider.model,
                    )
                )

            document.status = DocumentStatus.EMBEDDED.value
            document.next_processing_attempt_at = None
            document.last_processing_error = None
            document.updated_at = datetime.now(timezone.utc)
            self.db.commit()
            return len(vectors)
        except Exception as exc:
            self.db.rollback()
            self._mark_failed_or_retry(document, exc)
            self.db.commit()
            raise

    def _mark_in_progress(self, document: DocumentMetadata) -> None:
        document.status = DocumentStatus.EMBEDDING_IN_PROGRESS.value
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

        document.status = DocumentStatus.PROCESSED_TO_CHUNKS.value
        document.next_processing_attempt_at = self.retry_policy.next_attempt_at(
            document.processing_attempts
        )

