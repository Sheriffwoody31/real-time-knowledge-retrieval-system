package com.rag.ingestion.exception;

import java.time.Instant;

/**
 * Standard API error body for clients and logs correlation.
 */
public class ErrorResponse {
    private final String error;
    private final String message;
    private final Instant timestamp;

    public ErrorResponse(String error, String message, Instant timestamp) {
        this.error = error;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
