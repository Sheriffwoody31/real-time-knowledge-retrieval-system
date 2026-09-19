import uuid
from datetime import datetime, timezone
from typing import Optional

from sqlalchemy import delete, select
from sqlalchemy.orm import Session

from modules.documents.models import DocumentChunk, DocumentMetadata, Embedding
from modules.documents.status import DocumentStatus


class DocumentRepository:
    def __init__(self, db: Session) -> None:
        self.db = db

    def get(self, document_id: uuid.UUID) -> Optional[DocumentMetadata]:
        return self.db.get(DocumentMetadata, document_id)

    def get_by_idempotency_key(self, idempotency_key: str) -> Optional[DocumentMetadata]:
        statement = select(DocumentMetadata).where(
            DocumentMetadata.idempotency_key == idempotency_key
        )
        return self.db.scalar(statement)

    def add(self, document: DocumentMetadata) -> DocumentMetadata:
        self.db.add(document)
        return document

    def add_chunk(self, chunk: DocumentChunk) -> DocumentChunk:
        self.db.add(chunk)
        return chunk

    def add_embedding(self, embedding: Embedding) -> Embedding:
        self.db.add(embedding)
        return embedding

    def delete_chunks_for_document(self, document_id: uuid.UUID) -> None:
        self.db.execute(delete(DocumentChunk).where(DocumentChunk.document_id == document_id))

    def delete_embeddings_for_document(self, document_id: uuid.UUID, model: str) -> None:
        chunk_ids = select(DocumentChunk.id).where(DocumentChunk.document_id == document_id)
        self.db.execute(
            delete(Embedding).where(
                Embedding.chunk_id.in_(chunk_ids),
                Embedding.model == model,
            )
        )

    def list_chunks_for_document(self, document_id: uuid.UUID) -> list[DocumentChunk]:
        statement = (
            select(DocumentChunk)
            .where(DocumentChunk.document_id == document_id)
            .order_by(DocumentChunk.chunk_index)
        )
        return list(self.db.scalars(statement))

    def list_due_for_processing(
        self,
        *,
        limit: int,
        now: Optional[datetime] = None,
    ) -> list[DocumentMetadata]:
        effective_now = now or datetime.now(timezone.utc)
        statement = (
            select(DocumentMetadata)
            .where(DocumentMetadata.status == DocumentStatus.CREATED.value)
            .where(DocumentMetadata.processing_attempts < DocumentMetadata.max_processing_attempts)
            .where(
                (DocumentMetadata.next_processing_attempt_at.is_(None))
                | (DocumentMetadata.next_processing_attempt_at <= effective_now)
            )
            .order_by(DocumentMetadata.created_at)
            .limit(limit)
        )
        return list(self.db.scalars(statement))
