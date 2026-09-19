from pathlib import Path
from typing import Protocol

from modules.documents.models import DocumentMetadata
from modules.ingestion.constants import SourceType
from modules.processing.documents import RawDocument


class SourceFetcher(Protocol):
    def fetch(self, document: DocumentMetadata) -> RawDocument:
        pass


class FileServerSourceFetcher:
    def fetch(self, document: DocumentMetadata) -> RawDocument:
        path = self._resolve_path(document.source_uri)
        return RawDocument(content=path.read_bytes(), content_type=document.content_type)

    def _resolve_path(self, source_uri: str) -> Path:
        if source_uri.startswith("fileserver://"):
            return Path(source_uri.removeprefix("fileserver://"))
        if source_uri.startswith("file://"):
            return Path(source_uri.removeprefix("file://"))
        return Path(source_uri)


class UnsupportedSourceFetcher:
    def fetch(self, document: DocumentMetadata) -> RawDocument:
        raise ValueError(f"Unsupported source type: {document.source_type}")


class SourceFetcherRegistry:
    def __init__(self) -> None:
        self._fetchers: dict[str, SourceFetcher] = {
            SourceType.FILESERVER.value: FileServerSourceFetcher(),
        }

    def get(self, source_type: str) -> SourceFetcher:
        return self._fetchers.get(source_type, UnsupportedSourceFetcher())

