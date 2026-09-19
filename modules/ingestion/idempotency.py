import hashlib
from datetime import datetime
from typing import Optional


def _normalize(value: object) -> str:
    if value is None:
        return ""
    if isinstance(value, datetime):
        return value.isoformat()
    return str(value).strip().lower()


def build_document_idempotency_key(
    *,
    source_type: str,
    external_id: Optional[str],
    name: str,
    version: Optional[str],
    updated_at_source: Optional[datetime],
    checksum: Optional[str],
) -> str:
    parts = [
        source_type,
        external_id,
        name,
        version,
        updated_at_source,
        checksum,
    ]
    normalized = "|".join(_normalize(part) for part in parts)
    return hashlib.sha256(normalized.encode("utf-8")).hexdigest()

