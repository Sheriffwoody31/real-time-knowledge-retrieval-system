package com.rag.common.model;

import java.time.Instant;

public class DocumentChunkedEvent {
    private String documentId;
    private String originalFileName;
    private String sourceFilePath;
    private String extractedTextPath;
    private String chunkManifestPath;
    private String fileType;
    private String fileExtension;
    private int chunkCount;
    private String chunkingStrategy;
    private Instant uploadedAt;
    private Instant extractedAt;
    private Instant chunkedAt;
    private String source;

    public DocumentChunkedEvent() {
    }

    public DocumentChunkedEvent(
            String documentId,
            String originalFileName,
            String sourceFilePath,
            String extractedTextPath,
            String chunkManifestPath,
            String fileType,
            String fileExtension,
            int chunkCount,
            String chunkingStrategy,
            Instant uploadedAt,
            Instant extractedAt,
            Instant chunkedAt,
            String source
    ) {
        this.documentId = documentId;
        this.originalFileName = originalFileName;
        this.sourceFilePath = sourceFilePath;
        this.extractedTextPath = extractedTextPath;
        this.chunkManifestPath = chunkManifestPath;
        this.fileType = fileType;
        this.fileExtension = fileExtension;
        this.chunkCount = chunkCount;
        this.chunkingStrategy = chunkingStrategy;
        this.uploadedAt = uploadedAt;
        this.extractedAt = extractedAt;
        this.chunkedAt = chunkedAt;
        this.source = source;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getSourceFilePath() {
        return sourceFilePath;
    }

    public void setSourceFilePath(String sourceFilePath) {
        this.sourceFilePath = sourceFilePath;
    }

    public String getExtractedTextPath() {
        return extractedTextPath;
    }

    public void setExtractedTextPath(String extractedTextPath) {
        this.extractedTextPath = extractedTextPath;
    }

    public String getChunkManifestPath() {
        return chunkManifestPath;
    }

    public void setChunkManifestPath(String chunkManifestPath) {
        this.chunkManifestPath = chunkManifestPath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public int getChunkCount() {
        return chunkCount;
    }

    public void setChunkCount(int chunkCount) {
        this.chunkCount = chunkCount;
    }

    public String getChunkingStrategy() {
        return chunkingStrategy;
    }

    public void setChunkingStrategy(String chunkingStrategy) {
        this.chunkingStrategy = chunkingStrategy;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public Instant getExtractedAt() {
        return extractedAt;
    }

    public void setExtractedAt(Instant extractedAt) {
        this.extractedAt = extractedAt;
    }

    public Instant getChunkedAt() {
        return chunkedAt;
    }

    public void setChunkedAt(Instant chunkedAt) {
        this.chunkedAt = chunkedAt;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
