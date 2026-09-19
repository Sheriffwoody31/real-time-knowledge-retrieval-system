from fastapi import APIRouter, Request

router = APIRouter(tags=["health"])


@router.get("/health")
def health_check(request: Request) -> dict[str, str]:
    config = request.app.state.container.config
    return {
        "status": "ok",
        "app": config.app_name,
        "environment": config.app_env,
    }

