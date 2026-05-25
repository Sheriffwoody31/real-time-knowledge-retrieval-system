package com.rag.processing.producer;

import com.rag.common.model.DocumentTextExtractedEvent;
import com.rag.processing.config.ProcessingKafkaProperties;
import com.rag.processing.exception.DocumentProcessingException;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class DocumentTextExtractedEventProducer {

    private static final Logger log = LoggerFactory.getLogger(DocumentTextExtractedEventProducer.class);

    private final KafkaTemplate<String, DocumentTextExtractedEvent> kafkaTemplate;
    private final ProcessingKafkaProperties kafkaProperties;

    public DocumentTextExtractedEventProducer(
            KafkaTemplate<String, DocumentTextExtractedEvent> kafkaTemplate,
            ProcessingKafkaProperties kafkaProperties
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaProperties = kafkaProperties;
    }

    public void publish(DocumentTextExtractedEvent event) {
        String topic = kafkaProperties.getDocumentTextExtractedEventsTopic();
        log.info(
                "Publishing extracted text event documentId={} topic={} characters={}",
                event.getDocumentId(),
                topic,
                event.getCharacterCount()
        );

        try {
            SendResult<String, DocumentTextExtractedEvent> result = kafkaTemplate
                    .send(topic, event.getDocumentId(), event)
                    .get(kafkaProperties.getSendTimeoutSeconds(), TimeUnit.SECONDS);

            RecordMetadata metadata = result.getRecordMetadata();
            log.info(
                    "Extracted text event acknowledged documentId={} topic={} partition={} offset={}",
                    event.getDocumentId(),
                    metadata.topic(),
                    metadata.partition(),
                    metadata.offset()
            );
        } catch (TimeoutException e) {
            log.error(
                    "Extracted text event publish timed out after {}s documentId={} topic={}",
                    kafkaProperties.getSendTimeoutSeconds(),
                    event.getDocumentId(),
                    topic,
                    e
            );
            throw new DocumentProcessingException("Timed out publishing extracted text event.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while publishing extracted text event documentId={}", event.getDocumentId(), e);
            throw new DocumentProcessingException("Interrupted while publishing extracted text event.", e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            log.error("Failed to publish extracted text event documentId={} topic={}", event.getDocumentId(), topic, cause);
            throw new DocumentProcessingException("Failed to publish extracted text event.", cause);
        }
    }
}
