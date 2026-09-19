from enum import Enum


class IngestionChannel(str, Enum):
    API = "api"
    REDIS = "redis"
    KAFKA = "kafka"
    MCP = "mcp"


class SourceType(str, Enum):
    S3 = "s3"
    FILESERVER = "fileserver"
    MCP = "mcp"
    CLICKHOUSE = "clickhouse"


class FileFormat(str, Enum):
    TXT = "txt"
    CSV = "csv"
    XLSX = "xlsx"
    PARQUET = "parquet"
    PARQUET_GZIP = "parquet_gzip"
    JSON = "json"
    JSONL = "jsonl"
    MARKDOWN = "markdown"


class IngestionJobStatus(str, Enum):
    RECEIVED = "RECEIVED"
    REGISTERED = "REGISTERED"
    DUPLICATE = "DUPLICATE"
    FAILED = "FAILED"

