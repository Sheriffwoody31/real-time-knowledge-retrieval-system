package com.rag.ingestion.exception;

/**
 * Thrown when publishing a document event to Kafka fails.
 */
public class DocumentEventPublishException extends RuntimeException {

    public DocumentEventPublishException(String message, Throwable cause) {
        super(message, cause);
    }
}
