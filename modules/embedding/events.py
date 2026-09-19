import uuid
from dataclasses import dataclass
from typing import Protocol


@dataclass(frozen=True)
class EmbeddingRequestedEvent:
    document_id: uuid.UUID
    chunk_count: int
    embedding_model: str


class EmbeddingEventPublisher(Protocol):
    def publish(self, event: EmbeddingRequestedEvent) -> None:
        pass


class InMemoryEmbeddingEventPublisher:
    def __init__(self) -> None:
        self.events: list[EmbeddingRequestedEvent] = []

    def publish(self, event: EmbeddingRequestedEvent) -> None:
        self.events.append(event)

