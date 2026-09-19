from modules.processing.chunkers import FixedSizeTextChunker
from modules.processing.documents import ParsedDocument


def test_fixed_size_chunker_chunks_with_overlap() -> None:
    chunker = FixedSizeTextChunker(chunk_size=5, chunk_overlap=2)

    chunks = chunker.chunk(ParsedDocument(text="abcdefghij"))

    assert [chunk.content for chunk in chunks] == ["abcde", "defgh", "ghij"]
    assert chunks[0].metadata["start_offset"] == 0
    assert chunks[1].metadata["start_offset"] == 3


def test_fixed_size_chunker_returns_no_chunks_for_empty_text() -> None:
    chunker = FixedSizeTextChunker(chunk_size=5, chunk_overlap=2)

    assert chunker.chunk(ParsedDocument(text="")) == []

