from fastapi import FastAPI

from app.config import get_config
from app.container import build_container
from app.routers.db_health import router as db_health_router
from app.routers.health import router as health_router
from modules.ingestion.routes import router as ingestion_router
from modules.retrieval.routes import router as retrieval_router


def create_app() -> FastAPI:
    config = get_config()
    app = FastAPI(title=config.app_name, debug=config.app_debug)
    app.state.container = build_container(config)
    app.include_router(health_router)
    app.include_router(db_health_router)
    app.include_router(ingestion_router)
    app.include_router(retrieval_router)
    return app


app = create_app()
