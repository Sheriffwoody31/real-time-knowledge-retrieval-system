import uuid
from typing import Optional

from sqlalchemy.orm import Session

from modules.ingestion.models import IngestionJob


class IngestionJobRepository:
    def __init__(self, db: Session) -> None:
        self.db = db

    def get(self, job_id: uuid.UUID) -> Optional[IngestionJob]:
        return self.db.get(IngestionJob, job_id)

    def add(self, job: IngestionJob) -> IngestionJob:
        self.db.add(job)
        return job

    def commit(self) -> None:
        self.db.commit()

    def refresh(self, job: IngestionJob) -> None:
        self.db.refresh(job)
