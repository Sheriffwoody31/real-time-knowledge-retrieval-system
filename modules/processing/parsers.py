from typing import Protocol

from modules.ingestion.constants import FileFormat
from modules.processing.documents import ParsedDocument, RawDocument


class DocumentParser(Protocol):
    def parse(self, raw_document: RawDocument) -> ParsedDocument:
        pass


class TextDocumentParser:
    def parse(self, raw_document: RawDocument) -> ParsedDocument:
        return ParsedDocument(text=raw_document.content.decode("utf-8"))


class UnsupportedDocumentParser:
    def parse(self, raw_document: RawDocument) -> ParsedDocument:
        raise ValueError("Unsupported document parser")


class DocumentParserRegistry:
    def __init__(self) -> None:
        text_parser = TextDocumentParser()
        self._parsers: dict[str, DocumentParser] = {
            FileFormat.TXT.value: text_parser,
            FileFormat.MARKDOWN.value: text_parser,
        }

    def get(self, file_format: str) -> DocumentParser:
        return self._parsers.get(file_format, UnsupportedDocumentParser())

