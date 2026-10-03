from dataclasses import dataclass
from typing import Any, Optional

from sqlalchemy import select
from sqlalchemy.orm import Session

from modules.documents.models import DocumentChunk, DocumentMetadata, Embedding


@dataclass(frozen=True)
class RetrievalMatch:
    document_id: Any
    chunk_id: Any
    content: str
    score: float
    source_type: str
    source_uri: str
    document_name: str
    document_version: Optional[str]
    chunk_index: int
    metadata: dict[str, Any]


class RetrievalRepository:
    def __init__(self, db: Session) -> None:
        self.db = db

    def search_by_vector(
        self,
        *,
        query_vector: list[float],
        model: str,
        top_k: int,
        similarity_threshold: float,
    ) -> list[RetrievalMatch]:
        distance = Embedding.embedding.cosine_distance(query_vector)
        score = 1 - distance
        statement = (
            select(
                DocumentMetadata.id.label("document_id"),
                DocumentChunk.id.label("chunk_id"),
                DocumentChunk.content,
                score.label("score"),
                DocumentMetadata.source_type,
                DocumentMetadata.source_uri,
                DocumentMetadata.name.label("document_name"),
                DocumentMetadata.version.label("document_version"),
                DocumentChunk.chunk_index,
                DocumentChunk.chunk_metadata.label("metadata"),
            )
            .join(DocumentChunk, DocumentChunk.id == Embedding.chunk_id)
            .join(DocumentMetadata, DocumentMetadata.id == DocumentChunk.document_id)
            .where(Embedding.model == model)
            .where(score >= similarity_threshold)
            .order_by(distance)
            .limit(top_k)
        )

        return [
            RetrievalMatch(
                document_id=row.document_id,
                chunk_id=row.chunk_id,
                content=row.content,
                score=float(row.score),
                source_type=row.source_type,
                source_uri=row.source_uri,
                document_name=row.document_name,
                document_version=row.document_version,
                chunk_index=row.chunk_index,
                metadata=row.metadata,
            )
            for row in self.db.execute(statement)
        ]

