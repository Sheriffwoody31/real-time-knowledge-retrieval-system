from datetime import datetime
from typing import Any, Optional

from pydantic import BaseModel, Field

from modules.ingestion.constants import FileFormat, IngestionChannel, SourceType


class IngestDocumentCommand(BaseModel):
    ingestion_channel: IngestionChannel
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

