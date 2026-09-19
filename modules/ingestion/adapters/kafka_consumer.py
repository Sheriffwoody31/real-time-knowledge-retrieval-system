from modules.ingestion.commands import IngestDocumentCommand
from modules.ingestion.constants import IngestionChannel


def to_ingestion_command(payload: dict) -> IngestDocumentCommand:
    return IngestDocumentCommand(ingestion_channel=IngestionChannel.KAFKA, **payload)

