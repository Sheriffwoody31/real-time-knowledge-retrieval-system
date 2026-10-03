from typing import Optional

from sqlalchemy.orm import Session

from app.config import AppConfig
from modules.embedding.providers import DeterministicEmbeddingProvider, EmbeddingProvider
from modules.retrieval.repository import RetrievalRepository
from modules.retrieval.schemas import RetrievalSearchResponse, RetrievalSearchResult


class RetrievalService:
    def __init__(
        self,
        db: Session,
        config: AppConfig,
        embedding_provider: Optional[EmbeddingProvider] = None,
    ) -> None:
        self.db = db
        self.config = config
        self.embedding_provider = embedding_provider or DeterministicEmbeddingProvider()
        self.repository = RetrievalRepository(db)

    def search(
        self,
        *,
        query: str,
        top_k: Optional[int] = None,
        similarity_threshold: Optional[float] = None,
    ) -> RetrievalSearchResponse:
        effective_top_k = top_k or self.config.retrieval.default_top_k
        effective_threshold = (
            similarity_threshold
            if similarity_threshold is not None
            else self.config.retrieval.similarity_threshold
        )
        query_vector = self.embedding_provider.embed_texts([query])[0]
        matches = self.repository.search_by_vector(
            query_vector=query_vector,
            model=self.embedding_provider.model,
            top_k=effective_top_k,
            similarity_threshold=effective_threshold,
        )
        return RetrievalSearchResponse(
            query=query,
            embedding_model=self.embedding_provider.model,
            results=[
                RetrievalSearchResult(
                    document_id=match.document_id,
                    chunk_id=match.chunk_id,
                    content=match.content,
                    score=match.score,
                    source_type=match.source_type,
                    source_uri=match.source_uri,
                    document_name=match.document_name,
                    document_version=match.document_version,
                    chunk_index=match.chunk_index,
                    metadata=match.metadata,
                )
                for match in matches
            ],
        )

