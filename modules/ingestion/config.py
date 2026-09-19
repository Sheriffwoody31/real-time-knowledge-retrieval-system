from pydantic_settings import BaseSettings, SettingsConfigDict


class IngestionConfig(BaseSettings):
    max_upload_size_mb: int = 25
    allowed_file_types: str = "txt,md,pdf"

    model_config = SettingsConfigDict(env_prefix="INGESTION_", extra="ignore")

