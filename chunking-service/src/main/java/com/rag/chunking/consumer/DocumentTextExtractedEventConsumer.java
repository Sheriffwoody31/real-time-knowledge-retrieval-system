package com.rag.chunking.consumer;

import com.rag.chunking.service.DocumentChunkingService;
import com.rag.common.model.DocumentTextExtractedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DocumentTextExtractedEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(DocumentTextExtractedEventConsumer.class);

    private final DocumentChunkingService documentChunkingService;

    public DocumentTextExtractedEventConsumer(DocumentChunkingService documentChunkingService) {
        this.documentChunkingService = documentChunkingService;
    }

    @KafkaListener(
            topics = "${chunking.kafka.document-text-extracted-events-topic}",
            groupId = "${chunking.kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(DocumentTextExtractedEvent event) {
        log.info(
                "Received extracted text event documentId={} extractedTextPath={} characters={}",
                event != null ? event.getDocumentId() : null,
                event != null ? event.getExtractedTextPath() : null,
                event != null ? event.getCharacterCount() : null
        );

        try {
            documentChunkingService.process(event);
        } catch (RuntimeException e) {
            log.error(
                    "Document chunking failed documentId={} extractedTextPath={}",
                    event != null ? event.getDocumentId() : null,
                    event != null ? event.getExtractedTextPath() : null,
                    e
            );
            throw e;
        }
    }
}
