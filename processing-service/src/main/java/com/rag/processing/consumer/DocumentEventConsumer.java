package com.rag.processing.consumer;

import com.rag.common.model.DocumentEvent;
import com.rag.processing.service.DocumentProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DocumentEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(DocumentEventConsumer.class);

    private final DocumentProcessingService documentProcessingService;

    public DocumentEventConsumer(DocumentProcessingService documentProcessingService) {
        this.documentProcessingService = documentProcessingService;
    }

    @KafkaListener(
            topics = "${processing.kafka.raw-document-events-topic}",
            groupId = "${processing.kafka.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(DocumentEvent event) {

        log.info("Received document event: documentId={}, filePath={}, fileType={}",
                event.getDocumentId(),
                event.getFilePath(),
                event.getFileType()
        );

        try {
            documentProcessingService.process(event);
        } catch (RuntimeException e) {
            log.error(
                    "Document event processing failed documentId={} filePath={} fileType={}",
                    event != null ? event.getDocumentId() : null,
                    event != null ? event.getFilePath() : null,
                    event != null ? event.getFileType() : null,
                    e
            );
            throw e;
        }
    }
}
