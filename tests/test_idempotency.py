from datetime import datetime, timezone

from modules.ingestion.idempotency import build_document_idempotency_key


def test_idempotency_key_excludes_source_uri() -> None:
    updated_at_source = datetime(2026, 9, 20, 10, 30, tzinfo=timezone.utc)

    first_key = build_document_idempotency_key(
        source_type="s3",
        external_id="customer-events",
        name="events",
        version="v1",
        updated_at_source=updated_at_source,
        checksum="abc123",
    )
    second_key = build_document_idempotency_key(
        source_type="s3",
        external_id="customer-events",
        name="events",
        version="v1",
        updated_at_source=updated_at_source,
        checksum="abc123",
    )

    assert first_key == second_key


def test_idempotency_key_changes_when_document_version_changes() -> None:
    base_args = {
        "source_type": "clickhouse",
        "external_id": "events-export",
        "name": "events",
        "updated_at_source": None,
        "checksum": "abc123",
    }

    first_key = build_document_idempotency_key(version="v1", **base_args)
    second_key = build_document_idempotency_key(version="v2", **base_args)

    assert first_key != second_key

