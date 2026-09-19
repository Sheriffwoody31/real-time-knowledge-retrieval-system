"""create core rag tables

Revision ID: 202609190001
Revises:
Create Date: 2026-09-19
"""

from __future__ import annotations

from collections.abc import Sequence

import sqlalchemy as sa
from pgvector.sqlalchemy import Vector
from sqlalchemy.dialects import postgresql

from alembic import op

revision: str = "202609190001"
down_revision: str | None = None
branch_labels: str | Sequence[str] | None = None
depends_on: str | Sequence[str] | None = None


def timestamp_column(name: str, *, nullable: bool = False) -> sa.Column:
    return sa.Column(
        name,
        sa.DateTime(timezone=True),
        server_default=sa.func.now() if not nullable else None,
        nullable=nullable,
    )


def upgrade() -> None:
    op.execute("CREATE EXTENSION IF NOT EXISTS vector")

    op.create_table(
        "document_metadata",
        sa.Column("id", postgresql.UUID(as_uuid=True), primary_key=True),
        sa.Column("ingestion_channel", sa.String(length=30), nullable=False),
        sa.Column("source_type", sa.String(length=50), nullable=False),
        sa.Column("source_uri", sa.Text(), nullable=False),
        sa.Column("external_id", sa.String(length=300), nullable=True),
        sa.Column("name", sa.String(length=500), nullable=False),
        sa.Column("version", sa.String(length=100), nullable=True),
        sa.Column("updated_at_source", sa.DateTime(timezone=True), nullable=True),
        sa.Column("content_type", sa.String(length=150), nullable=True),
        sa.Column("file_format", sa.String(length=50), nullable=False),
        sa.Column("checksum", sa.String(length=128), nullable=True),
        sa.Column("size_bytes", sa.Integer(), nullable=True),
        sa.Column("idempotency_key", sa.String(length=64), nullable=False),
        sa.Column("status", sa.String(length=50), nullable=False),
        sa.Column("processing_attempts", sa.Integer(), nullable=False, server_default="0"),
        sa.Column("max_processing_attempts", sa.Integer(), nullable=False, server_default="5"),
        sa.Column("next_processing_attempt_at", sa.DateTime(timezone=True), nullable=True),
        sa.Column("last_processing_error", sa.Text(), nullable=True),
        sa.Column("metadata", postgresql.JSONB(astext_type=sa.Text()), nullable=False),
        timestamp_column("created_at"),
        timestamp_column("updated_at"),
        sa.UniqueConstraint("idempotency_key", name="uq_document_metadata_idempotency_key"),
    )
    op.create_index(
        "ix_document_metadata_ingestion_channel",
        "document_metadata",
        ["ingestion_channel"],
    )
    op.create_index("ix_document_metadata_source_type", "document_metadata", ["source_type"])
    op.create_index("ix_document_metadata_external_id", "document_metadata", ["external_id"])
    op.create_index(
        "ix_document_metadata_idempotency_key",
        "document_metadata",
        ["idempotency_key"],
    )
    op.create_index("ix_document_metadata_status", "document_metadata", ["status"])

    op.create_table(
        "document_chunks",
        sa.Column("id", postgresql.UUID(as_uuid=True), primary_key=True),
        sa.Column("document_id", postgresql.UUID(as_uuid=True), nullable=False),
        sa.Column("content", sa.Text(), nullable=False),
        sa.Column("chunk_index", sa.Integer(), nullable=False),
        sa.Column("metadata", postgresql.JSONB(astext_type=sa.Text()), nullable=False),
        timestamp_column("created_at"),
        sa.ForeignKeyConstraint(["document_id"], ["document_metadata.id"], ondelete="CASCADE"),
        sa.UniqueConstraint("document_id", "chunk_index", name="uq_document_chunks_document_index"),
    )
    op.create_index("ix_document_chunks_document_id", "document_chunks", ["document_id"])

    op.create_table(
        "embeddings",
        sa.Column("id", postgresql.UUID(as_uuid=True), primary_key=True),
        sa.Column("chunk_id", postgresql.UUID(as_uuid=True), nullable=False),
        sa.Column("embedding", Vector(1536), nullable=False),
        sa.Column("model", sa.String(length=100), nullable=False),
        timestamp_column("created_at"),
        sa.ForeignKeyConstraint(["chunk_id"], ["document_chunks.id"], ondelete="CASCADE"),
        sa.UniqueConstraint("chunk_id", "model", name="uq_embeddings_chunk_model"),
    )
    op.create_index("ix_embeddings_chunk_id", "embeddings", ["chunk_id"])
    op.create_index(
        "ix_embeddings_embedding_hnsw",
        "embeddings",
        ["embedding"],
        postgresql_using="hnsw",
        postgresql_with={"m": 16, "ef_construction": 64},
        postgresql_ops={"embedding": "vector_cosine_ops"},
    )

    op.create_table(
        "ingestion_jobs",
        sa.Column("id", postgresql.UUID(as_uuid=True), primary_key=True),
        sa.Column("document_id", postgresql.UUID(as_uuid=True), nullable=True),
        sa.Column("ingestion_channel", sa.String(length=30), nullable=False),
        sa.Column("status", sa.String(length=30), nullable=False),
        sa.Column("source_type", sa.String(length=50), nullable=False),
        sa.Column("source_uri", sa.Text(), nullable=False),
        sa.Column("error_message", sa.Text(), nullable=True),
        sa.Column("metadata", postgresql.JSONB(astext_type=sa.Text()), nullable=False),
        timestamp_column("created_at"),
        sa.Column("started_at", sa.DateTime(timezone=True), nullable=True),
        sa.Column("completed_at", sa.DateTime(timezone=True), nullable=True),
        sa.ForeignKeyConstraint(["document_id"], ["document_metadata.id"], ondelete="SET NULL"),
    )
    op.create_index("ix_ingestion_jobs_document_id", "ingestion_jobs", ["document_id"])
    op.create_index("ix_ingestion_jobs_ingestion_channel", "ingestion_jobs", ["ingestion_channel"])
    op.create_index("ix_ingestion_jobs_status", "ingestion_jobs", ["status"])
    op.create_index("ix_ingestion_jobs_source_type", "ingestion_jobs", ["source_type"])


def downgrade() -> None:
    op.drop_index("ix_ingestion_jobs_source_type", table_name="ingestion_jobs")
    op.drop_index("ix_ingestion_jobs_status", table_name="ingestion_jobs")
    op.drop_index("ix_ingestion_jobs_ingestion_channel", table_name="ingestion_jobs")
    op.drop_index("ix_ingestion_jobs_document_id", table_name="ingestion_jobs")
    op.drop_table("ingestion_jobs")

    op.drop_index("ix_embeddings_embedding_hnsw", table_name="embeddings", postgresql_using="hnsw")
    op.drop_index("ix_embeddings_chunk_id", table_name="embeddings")
    op.drop_table("embeddings")

    op.drop_index("ix_document_chunks_document_id", table_name="document_chunks")
    op.drop_table("document_chunks")

    op.drop_index("ix_document_metadata_status", table_name="document_metadata")
    op.drop_index("ix_document_metadata_idempotency_key", table_name="document_metadata")
    op.drop_index("ix_document_metadata_external_id", table_name="document_metadata")
    op.drop_index("ix_document_metadata_source_type", table_name="document_metadata")
    op.drop_index("ix_document_metadata_ingestion_channel", table_name="document_metadata")
    op.drop_table("document_metadata")
