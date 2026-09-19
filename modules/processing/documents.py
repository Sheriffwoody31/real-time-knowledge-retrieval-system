from dataclasses import dataclass
from typing import Optional


@dataclass(frozen=True)
class RawDocument:
    content: bytes
    content_type: Optional[str] = None


@dataclass(frozen=True)
class ParsedDocument:
    text: str


@dataclass(frozen=True)
class ChunkData:
    content: str
    metadata: dict
