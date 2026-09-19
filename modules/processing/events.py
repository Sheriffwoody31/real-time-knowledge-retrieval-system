import uuid
from dataclasses import dataclass
from typing import Protocol


@dataclass(frozen=True)
class ProcessingRequestedEvent:
    document_id: uuid.UUID
    source_type: str
    source_uri: str
    file_format: str
    idempotency_key: str


class ProcessingEventPublisher(Protocol):
    def publish(self, event: ProcessingRequestedEvent) -> None:
        pass


class InMemoryProcessingEventPublisher:
    def __init__(self) -> None:
        self.events: list[ProcessingRequestedEvent] = []

    def publish(self, event: ProcessingRequestedEvent) -> None:
        self.events.append(event)

