from typing import Annotated

from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session

from modules.ingestion.commands import IngestDocumentCommand
from modules.ingestion.constants import IngestionChannel
from modules.ingestion.schemas import RegisterDocumentRequest, RegisterDocumentResponse
from modules.ingestion.service import IngestionService
from shared.database import get_db_session

router = APIRouter(prefix="/api/v1/ingestion", tags=["ingestion"])
DatabaseSession = Annotated[Session, Depends(get_db_session)]


@router.post("/documents", response_model=RegisterDocumentResponse)
def register_document(
    request: RegisterDocumentRequest,
    db: DatabaseSession,
) -> RegisterDocumentResponse:
    command = IngestDocumentCommand(
        ingestion_channel=IngestionChannel.API,
        **request.model_dump(),
    )
    result = IngestionService(db).register_document(command)
    return RegisterDocumentResponse(
        job_id=result.job.id,
        document_id=result.document.id,
        status=result.job.status,
        document_status=result.document.status,
        idempotency_key=result.document.idempotency_key,
        duplicate=result.duplicate,
    )
