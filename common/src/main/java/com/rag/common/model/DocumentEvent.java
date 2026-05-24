package com.rag.common.model;

import java.time.Instant;

public class DocumentEvent {
    private String documentId;
    private String originalFileName;
    private String filePath;
    private String fileType;
    private String fileExtension;
    private Instant uploadedAt;
    private String source;

    public DocumentEvent() {
    }

    public DocumentEvent(String documentId, String filePath, String fileType, Instant uploadedAt, String source) {
        this(documentId, null, filePath, fileType, null, uploadedAt, source);
    }

    public DocumentEvent(
            String documentId,
            String originalFileName,
            String filePath,
            String fileType,
            String fileExtension,
            Instant uploadedAt,
            String source
    ) {
        this.documentId = documentId;
        this.originalFileName = originalFileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.fileExtension = fileExtension;
        this.uploadedAt = uploadedAt;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
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

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
