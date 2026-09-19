from typing import Annotated

from fastapi import APIRouter, Depends
from sqlalchemy import text
from sqlalchemy.orm import Session

from shared.database import get_db_session

router = APIRouter(tags=["health"])
DatabaseSession = Annotated[Session, Depends(get_db_session)]


@router.get("/health/db")
def database_health_check(db: DatabaseSession) -> dict[str, str]:
    db.execute(text("SELECT 1"))
    return {"status": "ok", "database": "postgres"}
