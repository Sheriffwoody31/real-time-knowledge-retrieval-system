# Import model modules so Alembic can discover their table metadata.
from modules.documents.models import DocumentChunk, DocumentMetadata, Embedding  # noqa: F401, E402
from modules.ingestion.models import IngestionJob  # noqa: F401, E402
from shared.database import Base

__all__ = ["Base"]
