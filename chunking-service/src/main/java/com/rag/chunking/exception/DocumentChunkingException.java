package com.rag.chunking.exception;

public class DocumentChunkingException extends RuntimeException {

    public DocumentChunkingException(String message) {
        super(message);
    }

    public DocumentChunkingException(String message, Throwable cause) {
        super(message, cause);
    }
}
