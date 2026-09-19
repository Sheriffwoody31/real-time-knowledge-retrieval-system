from app.config import AppConfig


def test_database_url_can_be_derived_from_postgres_settings() -> None:
    config = AppConfig(database_url=None)

    assert config.sqlalchemy_database_url == "postgresql+psycopg://rag:rag@localhost:5433/rag"


def test_explicit_database_url_takes_precedence() -> None:
    config = AppConfig(database_url="postgresql+psycopg://user:pass@db:5432/app")

    assert config.sqlalchemy_database_url == "postgresql+psycopg://user:pass@db:5432/app"
