from __future__ import annotations

from functools import lru_cache
from typing import Optional

from pydantic import Field
from pydantic_settings import BaseSettings, SettingsConfigDict

from modules.auth.config import AuthConfig
from modules.ingestion.config import IngestionConfig
from modules.jobs.config import JobsConfig
from modules.processing.config import ProcessingConfig
from modules.retrieval.config import RetrievalConfig
from modules.sources.config import SourcesConfig


class AppConfig(BaseSettings):
    app_name: str = "real-time-knowledge-retrieval-system"
    app_env: str = "local"
    app_debug: bool = False
    app_api_prefix: str = "/api/v1"

    postgres_db: str = "rag"
    postgres_user: str = "rag"
    postgres_password: str = "rag"
    postgres_host: str = "localhost"
    postgres_port: int = 5433
    database_url: Optional[str] = None

    redis_host: str = "localhost"
    redis_port: int = 6379
    redis_url: Optional[str] = None

    auth: AuthConfig = Field(default_factory=AuthConfig)
    ingestion: IngestionConfig = Field(default_factory=IngestionConfig)
    jobs: JobsConfig = Field(default_factory=JobsConfig)
    processing: ProcessingConfig = Field(default_factory=ProcessingConfig)
    retrieval: RetrievalConfig = Field(default_factory=RetrievalConfig)
    sources: SourcesConfig = Field(default_factory=SourcesConfig)

    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8", extra="ignore")

    @property
    def sqlalchemy_database_url(self) -> str:
        if self.database_url:
            return self.database_url
        return (
            f"postgresql+psycopg://{self.postgres_user}:{self.postgres_password}"
            f"@{self.postgres_host}:{self.postgres_port}/{self.postgres_db}"
        )

    @property
    def redis_connection_url(self) -> str:
        if self.redis_url:
            return self.redis_url
        return f"redis://{self.redis_host}:{self.redis_port}/0"


@lru_cache
def get_config() -> AppConfig:
    return AppConfig()
