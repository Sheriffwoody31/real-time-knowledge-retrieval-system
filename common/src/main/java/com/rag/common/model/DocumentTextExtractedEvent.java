package com.rag.common.model;

import java.time.Instant;

public class DocumentTextExtractedEvent {
    private String documentId;
    private String originalFileName;
    private String sourceFilePath;
    private String extractedTextPath;
    private String fileType;
    private String fileExtension;
    private long characterCount;
    private Instant uploadedAt;
    private Instant extractedAt;
    private String source;

    public DocumentTextExtractedEvent() {
    }

    public DocumentTextExtractedEvent(
            String documentId,
            String originalFileName,
            String sourceFilePath,
            String extractedTextPath,
            String fileType,
            String fileExtension,
            long characterCount,
            Instant uploadedAt,
            Instant extractedAt,
            String source
    ) {
        this.documentId = documentId;
        this.originalFileName = originalFileName;
        this.sourceFilePath = sourceFilePath;
        this.extractedTextPath = extractedTextPath;
        this.fileType = fileType;
        this.fileExtension = fileExtension;
        this.characterCount = characterCount;
        this.uploadedAt = uploadedAt;
        this.extractedAt = extractedAt;
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

    public long getCharacterCount() {
        return characterCount;
    }

    public void setCharacterCount(long characterCount) {
        this.characterCount = characterCount;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
