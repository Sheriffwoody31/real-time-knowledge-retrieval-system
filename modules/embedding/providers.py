import hashlib
from typing import Protocol


class EmbeddingProvider(Protocol):
    @property
    def model(self) -> str:
        pass

    @property
    def dimensions(self) -> int:
        pass

    def embed_texts(self, texts: list[str]) -> list[list[float]]:
        pass


class DeterministicEmbeddingProvider:
    def __init__(
        self,
        *,
        model: str = "local-deterministic-embedding",
        dimensions: int = 1536,
    ) -> None:
        self._model = model
        self._dimensions = dimensions

    @property
    def model(self) -> str:
        return self._model

    @property
    def dimensions(self) -> int:
        return self._dimensions

    def embed_texts(self, texts: list[str]) -> list[list[float]]:
        return [self._embed_text(text) for text in texts]

    def _embed_text(self, text: str) -> list[float]:
        digest = hashlib.sha256(text.encode("utf-8")).digest()
        values: list[float] = []

        while len(values) < self.dimensions:
            for byte in digest:
                values.append((byte / 255.0) * 2.0 - 1.0)
                if len(values) == self.dimensions:
                    break
            digest = hashlib.sha256(digest).digest()

        return values
