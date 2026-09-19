from typing import Protocol

from modules.processing.documents import ChunkData, ParsedDocument


class Chunker(Protocol):
    def chunk(self, parsed_document: ParsedDocument) -> list[ChunkData]:
        pass


class FixedSizeTextChunker:
    def __init__(self, *, chunk_size: int, chunk_overlap: int) -> None:
        if chunk_size <= 0:
            raise ValueError("chunk_size must be greater than 0")
        if chunk_overlap < 0:
            raise ValueError("chunk_overlap cannot be negative")
        if chunk_overlap >= chunk_size:
            raise ValueError("chunk_overlap must be smaller than chunk_size")
        self.chunk_size = chunk_size
        self.chunk_overlap = chunk_overlap

    def chunk(self, parsed_document: ParsedDocument) -> list[ChunkData]:
        text = parsed_document.text
        if not text:
            return []

        chunks: list[ChunkData] = []
        step = self.chunk_size - self.chunk_overlap
        start = 0

        while start < len(text):
            end = min(start + self.chunk_size, len(text))
            chunks.append(
                ChunkData(
                    content=text[start:end],
                    metadata={
                        "chunking_strategy": "fixed_size",
                        "start_offset": start,
                        "end_offset": end,
                    },
                )
            )
            if end == len(text):
                break
            start += step

        return chunks


class ChunkerRegistry:
    def __init__(self, *, chunk_size: int, chunk_overlap: int) -> None:
        self._chunkers: dict[str, Chunker] = {
            "fixed_size": FixedSizeTextChunker(
                chunk_size=chunk_size,
                chunk_overlap=chunk_overlap,
            )
        }

    def get(self, strategy: str) -> Chunker:
        try:
            return self._chunkers[strategy]
        except KeyError as exc:
            raise ValueError(f"Unsupported chunking strategy: {strategy}") from exc

