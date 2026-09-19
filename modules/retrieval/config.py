from pydantic_settings import BaseSettings, SettingsConfigDict


class RetrievalConfig(BaseSettings):
    default_top_k: int = 5
    similarity_threshold: float = 0.75

    model_config = SettingsConfigDict(env_prefix="RETRIEVAL_", extra="ignore")

