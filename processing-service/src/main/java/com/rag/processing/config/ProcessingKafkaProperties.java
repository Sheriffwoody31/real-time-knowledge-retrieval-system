package com.rag.processing.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "processing.kafka")
public class ProcessingKafkaProperties {
    private String rawDocumentEventsTopic = "raw-doc-events";
    private String documentTextExtractedEventsTopic = "document-text-extracted-events";
    private String groupId = "processing-group";
    private String trustedPackages = "com.rag.common.model";
    private long sendTimeoutSeconds = 30L;

    public String getRawDocumentEventsTopic() {
        return rawDocumentEventsTopic;
    }

    public void setRawDocumentEventsTopic(String rawDocumentEventsTopic) {
        this.rawDocumentEventsTopic = rawDocumentEventsTopic;
    }

    public String getDocumentTextExtractedEventsTopic() {
        return documentTextExtractedEventsTopic;
    }

    public void setDocumentTextExtractedEventsTopic(String documentTextExtractedEventsTopic) {
        this.documentTextExtractedEventsTopic = documentTextExtractedEventsTopic;
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
