# Real-Time Knowledge Retrieval System

A modular monolith RAG backend for ingesting knowledge sources, processing content asynchronously, storing embeddings, and retrieving grounded answers.

## Phase 1 Scope

- FastAPI application shell
- Modular project layout
- App-level and module-level configuration
- Health endpoint
- Docker Compose for PostgreSQL and Redis
- SQLAlchemy and Alembic database foundation
- PGVector-ready schema for document embeddings
- Metadata-first ingestion registration with document idempotency
- Processing dispatch lifecycle for `CREATED` documents
- Processing execution skeleton for fetching, parsing, chunking, and embedding handoff
- Retrieval search over stored pgvector embeddings

## Local Development

```bash
python3 -m venv .venv
source .venv/bin/activate
pip install -e ".[dev]"
cp .env.example .env
docker compose up -d
alembic upgrade head
uvicorn app.main:app --reload
```

Health check:

```bash
curl http://localhost:8000/health
curl http://localhost:8000/health/db
```

Register document metadata for ingestion:

```bash
curl -X POST http://localhost:8000/api/v1/ingestion/documents \
  -H "Content-Type: application/json" \
  -d '{
    "source_type": "s3",
    "source_uri": "s3://bucket/path/data.parquet.gz",
    "name": "customer-events",
    "version": "2026-09-20",
    "file_format": "parquet_gzip",
    "checksum": "abc123"
  }'
```

Search embedded chunks:

```bash
curl -X POST http://localhost:8000/api/v1/retrieval/search \
  -H "Content-Type: application/json" \
  -d '{
    "query": "customer events",
    "top_k": 5
  }'
```
