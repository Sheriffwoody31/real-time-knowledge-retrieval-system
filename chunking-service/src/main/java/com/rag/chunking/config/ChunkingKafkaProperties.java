package com.rag.chunking.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "chunking.kafka")
public class ChunkingKafkaProperties {
    private String documentTextExtractedEventsTopic = "document-text-extracted-events";
    private String documentChunkedEventsTopic = "document-chunked-events";
    private String groupId = "chunking-group";
    private String trustedPackages = "com.rag.common.model";
    private long sendTimeoutSeconds = 30L;

    public String getDocumentTextExtractedEventsTopic() {
        return documentTextExtractedEventsTopic;
    }

    public void setDocumentTextExtractedEventsTopic(String documentTextExtractedEventsTopic) {
        this.documentTextExtractedEventsTopic = documentTextExtractedEventsTopic;
    }

    public String getDocumentChunkedEventsTopic() {
        return documentChunkedEventsTopic;
    }

    public void setDocumentChunkedEventsTopic(String documentChunkedEventsTopic) {
        this.documentChunkedEventsTopic = documentChunkedEventsTopic;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getTrustedPackages() {
        return trustedPackages;
    }

    public void setTrustedPackages(String trustedPackages) {
        this.trustedPackages = trustedPackages;
    }

    public long getSendTimeoutSeconds() {
        return sendTimeoutSeconds;
    }

    public void setSendTimeoutSeconds(long sendTimeoutSeconds) {
        this.sendTimeoutSeconds = sendTimeoutSeconds;
    }
}
