import uuid
from typing import Any, Optional

from pydantic import BaseModel, Field


class RetrievalSearchRequest(BaseModel):
    query: str
    top_k: Optional[int] = Field(default=None, gt=0, le=50)
    similarity_threshold: Optional[float] = Field(default=None, ge=-1.0, le=1.0)


class RetrievalSearchResult(BaseModel):
    document_id: uuid.UUID
    chunk_id: uuid.UUID
    content: str
    score: float
    source_type: str
    source_uri: str
    document_name: str
    document_version: Optional[str]
    chunk_index: int
    metadata: dict[str, Any]


class RetrievalSearchResponse(BaseModel):
    query: str
    embedding_model: str
    results: list[RetrievalSearchResult]

