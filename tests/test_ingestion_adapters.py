from modules.ingestion.adapters.kafka_consumer import to_ingestion_command as kafka_command
from modules.ingestion.adapters.mcp_adapter import to_ingestion_command as mcp_command
from modules.ingestion.adapters.redis_consumer import to_ingestion_command as redis_command
from modules.ingestion.constants import IngestionChannel


def _payload() -> dict:
    return {
        "source_type": "s3",
        "source_uri": "s3://bucket/path/file.csv",
        "name": "file.csv",
        "file_format": "csv",
    }


def test_redis_adapter_sets_channel() -> None:
    assert redis_command(_payload()).ingestion_channel == IngestionChannel.REDIS


def test_kafka_adapter_sets_channel() -> None:
    assert kafka_command(_payload()).ingestion_channel == IngestionChannel.KAFKA


def test_mcp_adapter_sets_channel() -> None:
    assert mcp_command(_payload()).ingestion_channel == IngestionChannel.MCP

