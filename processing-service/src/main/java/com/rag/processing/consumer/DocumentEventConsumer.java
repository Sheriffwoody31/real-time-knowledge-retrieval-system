package com.rag.processing.consumer;

import com.rag.common.model.DocumentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DocumentEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(DocumentEventConsumer.class);

    @KafkaListener(
            topics = "raw-doc-events",
            groupId = "processing-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(DocumentEvent event) {

        log.info("Received document event: documentId={}, filePath={}, fileType={}",
                event.getDocumentId(),
                event.getFilePath(),
                event.getFileType()
        );

        // Next step: fetch file + process
    }
}