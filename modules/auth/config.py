from pydantic_settings import BaseSettings, SettingsConfigDict


class AuthConfig(BaseSettings):
    api_key_header: str = "X-API-Key"

    model_config = SettingsConfigDict(env_prefix="AUTH_", extra="ignore")

