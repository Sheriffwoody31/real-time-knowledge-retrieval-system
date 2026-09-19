from modules.embedding.providers import DeterministicEmbeddingProvider


def test_deterministic_embedding_provider_is_repeatable() -> None:
    provider = DeterministicEmbeddingProvider(dimensions=8)

    first = provider.embed_texts(["hello"])[0]
    second = provider.embed_texts(["hello"])[0]

    assert first == second
    assert len(first) == 8


def test_deterministic_embedding_provider_varies_by_text() -> None:
    provider = DeterministicEmbeddingProvider(dimensions=8)

    first = provider.embed_texts(["hello"])[0]
    second = provider.embed_texts(["world"])[0]

    assert first != second

