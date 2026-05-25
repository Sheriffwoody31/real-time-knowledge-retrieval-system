package com.rag.processing.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "processing.storage")
public class ProcessingStorageProperties {
    private String extractedTextRoot = "../storage/extracted";

    public String getExtractedTextRoot() {
        return extractedTextRoot;
    }

    public void setExtractedTextRoot(String extractedTextRoot) {
        this.extractedTextRoot = extractedTextRoot;
    }
}
