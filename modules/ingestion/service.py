from datetime import datetime, timezone

from sqlalchemy.orm import Session

from modules.documents.models import DocumentMetadata
from modules.documents.repository import DocumentRepository
from modules.documents.status import DocumentStatus
from modules.ingestion.commands import IngestDocumentCommand
from modules.ingestion.constants import IngestionJobStatus
from modules.ingestion.idempotency import build_document_idempotency_key
from modules.ingestion.models import IngestionJob
from modules.ingestion.repository import IngestionJobRepository


class IngestionRegistrationResult:
    def __init__(
        self,
        *,
        job: IngestionJob,
        document: DocumentMetadata,
        duplicate: bool,
    ) -> None:
        self.job = job
        self.document = document
        self.duplicate = duplicate


class IngestionService:
    def __init__(self, db: Session) -> None:
        self.db = db
        self.documents = DocumentRepository(db)
        self.jobs = IngestionJobRepository(db)

    def register_document(self, command: IngestDocumentCommand) -> IngestionRegistrationResult:
        idempotency_key = build_document_idempotency_key(
            source_type=command.source_type.value,
            external_id=command.external_id,
            name=command.name,
            version=command.version,
            updated_at_source=command.updated_at_source,
            checksum=command.checksum,
        )
        existing_document = self.documents.get_by_idempotency_key(idempotency_key)
        duplicate = existing_document is not None

        if existing_document is None:
            document = DocumentMetadata(
                ingestion_channel=command.ingestion_channel.value,
                source_type=command.source_type.value,
                source_uri=command.source_uri,
                external_id=command.external_id,
                name=command.name,
                version=command.version,
                updated_at_source=command.updated_at_source,
                content_type=command.content_type,
                file_format=command.file_format.value,
                checksum=command.checksum,
                size_bytes=command.size_bytes,
                idempotency_key=idempotency_key,
                status=DocumentStatus.CREATED.value,
                next_processing_attempt_at=datetime.now(timezone.utc),
                document_metadata=command.metadata,
            )
            self.documents.add(document)
            job_status = IngestionJobStatus.REGISTERED.value
        else:
            document = existing_document
            job_status = IngestionJobStatus.DUPLICATE.value

        job = IngestionJob(
            document=document,
            ingestion_channel=command.ingestion_channel.value,
            status=job_status,
            source_type=command.source_type.value,
            source_uri=command.source_uri,
            job_metadata=command.metadata,
            completed_at=datetime.now(timezone.utc),
        )
        self.jobs.add(job)
        self.db.commit()
        self.db.refresh(document)
        self.db.refresh(job)

        return IngestionRegistrationResult(job=job, document=document, duplicate=duplicate)

