from typing import Annotated

from fastapi import APIRouter, Depends, Request
from sqlalchemy.orm import Session

from modules.retrieval.schemas import RetrievalSearchRequest, RetrievalSearchResponse
from modules.retrieval.service import RetrievalService
from shared.database import get_db_session

router = APIRouter(prefix="/api/v1/retrieval", tags=["retrieval"])
DatabaseSession = Annotated[Session, Depends(get_db_session)]


@router.post("/search", response_model=RetrievalSearchResponse)
def search(
    request: RetrievalSearchRequest,
    http_request: Request,
    db: DatabaseSession,
) -> RetrievalSearchResponse:
    config = http_request.app.state.container.config
    return RetrievalService(db, config).search(
        query=request.query,
        top_k=request.top_k,
        similarity_threshold=request.similarity_threshold,
    )

