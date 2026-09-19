from pydantic_settings import BaseSettings, SettingsConfigDict


class JobsConfig(BaseSettings):
    queue_name: str = "rag-jobs"
    max_retries: int = 3

    model_config = SettingsConfigDict(env_prefix="JOBS_", extra="ignore")

