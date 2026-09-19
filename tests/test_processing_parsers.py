from modules.processing.documents import RawDocument
from modules.processing.parsers import TextDocumentParser


def test_text_document_parser_decodes_utf8() -> None:
    parser = TextDocumentParser()

    parsed = parser.parse(RawDocument(content=b"hello world", content_type="text/plain"))

    assert parsed.text == "hello world"

