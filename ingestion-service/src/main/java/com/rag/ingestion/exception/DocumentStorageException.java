package com.rag.ingestion.exception;

/**
 * Thrown when persisting an uploaded file to local storage fails.
 */
public class DocumentStorageException extends RuntimeException {

    public DocumentStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
