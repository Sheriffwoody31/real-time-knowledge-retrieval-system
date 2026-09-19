from datetime import datetime, timezone
from typing import Optional

from sqlalchemy.orm import Session

from modules.documents.repository import DocumentRepository
from modules.documents.status import DocumentStatus
from modules.processing.events import ProcessingEventPublisher, ProcessingRequestedEvent
from modules.processing.retry_policy import ExponentialBackoffRetryPolicy


class ProcessingDispatchService:
    def __init__(
        self,
        db: Session,
        publisher: ProcessingEventPublisher,
        retry_policy: Optional[ExponentialBackoffRetryPolicy] = None,
    ) -> None:
        self.db = db
        self.documents = DocumentRepository(db)
        self.publisher = publisher
        self.retry_policy = retry_policy or ExponentialBackoffRetryPolicy()

    def dispatch_created_documents(self, *, limit: int = 100) -> int:
        documents = self.documents.list_due_for_processing(limit=limit)
        dispatched_count = 0

        for document in documents:
            try:
                self.publisher.publish(
                    ProcessingRequestedEvent(
                        document_id=document.id,
                        source_type=document.source_type,
                        source_uri=document.source_uri,
                        file_format=document.file_format,
                        idempotency_key=document.idempotency_key,
                    )
                )
            except Exception as exc:
                document.processing_attempts += 1
                document.last_processing_error = str(exc)
                document.next_processing_attempt_at = self.retry_policy.next_attempt_at(
                    document.processing_attempts
                )
                if document.processing_attempts >= document.max_processing_attempts:
                    document.status = DocumentStatus.FAILED.value
                continue

            document.status = DocumentStatus.PROCESSING_REQUESTED.value
            document.next_processing_attempt_at = None
            document.last_processing_error = None
            document.updated_at = datetime.now(timezone.utc)
            dispatched_count += 1

        self.db.commit()
        return dispatched_count
