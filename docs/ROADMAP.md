# Roadmap

This project currently has the core RAG spine in place:

```text
ingest -> process -> chunk -> embed -> retrieve
```

The remaining work should focus on completing one high-quality vertical path before broadening source and transport support.

## Next Priority

Build answer generation with citations.

Target endpoint:

```text
POST /api/v1/retrieval/ask
```

Expected flow:

```text
question -> retrieval search -> context assembly -> LLM answer -> citations
```

## Remaining Work

### Answer Generation

- Add chat/LLM provider interface.
- Add local/test provider for deterministic tests.
- Add OpenAI chat provider.
- Build context assembly from retrieved chunks.
- Return answer text with source citations.

### Real Providers

- Add OpenAI embedding provider.
- Add provider config for embedding and chat models.
- Add API key handling through environment configuration.
- Add batching, retry behavior, and provider error handling.

### Worker Runtime

- Add processing worker entrypoint.
- Add embedding worker entrypoint.
- Add scheduled dispatch runner.
- Add Redis/Kafka consumer runners after the local path is stable.

### Source Connectors

- Implement S3 source fetcher.
- Implement ClickHouse source fetcher.
- Implement MCP source fetcher.
- Keep source fetching isolated from ingestion and processing domain logic.

### File Parsers

- Add CSV parser.
- Add XLSX parser.
- Add JSON/JSONL parser.
- Add Parquet and gzipped Parquet parser.

### Operational APIs

- Add document list/detail endpoints.
- Add document chunk inspection endpoint.
- Add ingestion job detail endpoint.
- Add manual processing dispatch endpoint.
- Add manual embedding trigger endpoint.

### Production Quality

- Add structured logging.
- Add request IDs.
- Add consistent API error responses.
- Add integration tests with PostgreSQL/pgvector.
- Add CI checks for tests and linting.
- Expand README with architecture and lifecycle diagrams.

