package com.rag.ingestion.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ingestion.kafka")
public class IngestionKafkaProperties {
    private String rawDocumentEventsTopic = "raw-doc-events";
    private long sendTimeoutSeconds = 30L;

    public String getRawDocumentEventsTopic() {
        return rawDocumentEventsTopic;
    }

    public void setRawDocumentEventsTopic(String rawDocumentEventsTopic) {
        this.rawDocumentEventsTopic = rawDocumentEventsTopic;
    }

    public long getSendTimeoutSeconds() {
        return sendTimeoutSeconds;
    }

    public void setSendTimeoutSeconds(long sendTimeoutSeconds) {
        this.sendTimeoutSeconds = sendTimeoutSeconds;
    }
}
