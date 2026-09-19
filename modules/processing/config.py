from pydantic_settings import BaseSettings, SettingsConfigDict


class ProcessingConfig(BaseSettings):
    chunk_size: int = 1000
    chunk_overlap: int = 150
    chunking_strategy: str = "fixed_size"
    embedding_model: str = "text-embedding-3-small"

    model_config = SettingsConfigDict(env_prefix="PROCESSING_", extra="ignore")
