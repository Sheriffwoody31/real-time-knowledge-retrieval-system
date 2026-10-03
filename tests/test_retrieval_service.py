from app.config import AppConfig
from modules.retrieval.repository import RetrievalMatch
from modules.retrieval.service import RetrievalService


class StubEmbeddingProvider:
    model = "stub-model"
    dimensions = 3

    def embed_texts(self, texts: list[str]) -> list[list[float]]:
        return [[1.0, 0.0, 0.0] for _ in texts]


class StubRetrievalRepository:
    def __init__(self) -> None:
        self.search_args = None

    def search_by_vector(self, **kwargs) -> list[RetrievalMatch]:
        self.search_args = kwargs
        return []


def test_retrieval_service_uses_provider_and_config_defaults() -> None:
    service = RetrievalService(
        db=None,
        config=AppConfig(),
        embedding_provider=StubEmbeddingProvider(),
    )
    repository = StubRetrievalRepository()
    service.repository = repository

    response = service.search(query="hello")

    assert response.embedding_model == "stub-model"
    assert repository.search_args["query_vector"] == [1.0, 0.0, 0.0]
    assert repository.search_args["top_k"] == 5
    assert repository.search_args["similarity_threshold"] == 0.75

