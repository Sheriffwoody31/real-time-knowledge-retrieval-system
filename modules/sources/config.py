from pydantic_settings import BaseSettings, SettingsConfigDict


class SourcesConfig(BaseSettings):
    github_api_base_url: str = "https://api.github.com"
    request_timeout_seconds: int = 30

    model_config = SettingsConfigDict(env_prefix="SOURCES_", extra="ignore")

