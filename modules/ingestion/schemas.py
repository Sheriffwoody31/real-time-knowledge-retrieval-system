import uuid
from datetime import datetime
from typing import Any, Optional

from pydantic import BaseModel, Field

from modules.ingestion.constants import FileFormat, SourceType


class RegisterDocumentRequest(BaseModel):
    source_type: SourceType
    source_uri: str
    name: str
    file_format: FileFormat
    external_id: Optional[str] = None
    version: Optional[str] = None
    updated_at_source: Optional[datetime] = None
    content_type: Optional[str] = None
    checksum: Optional[str] = None
    size_bytes: Optional[int] = None
    metadata: dict[str, Any] = Field(default_factory=dict)


class RegisterDocumentResponse(BaseModel):
    job_id: uuid.UUID
    document_id: uuid.UUID
    status: str
    document_status: str
    idempotency_key: str
    duplicate: bool

