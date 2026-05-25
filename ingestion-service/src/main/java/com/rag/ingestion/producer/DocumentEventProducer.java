package com.rag.ingestion.producer;

import com.rag.common.model.DocumentEvent;
import com.rag.ingestion.config.IngestionKafkaProperties;
import com.rag.ingestion.exception.DocumentEventPublishException;
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
public class DocumentEventProducer {

    private static final Logger log = LoggerFactory.getLogger(DocumentEventProducer.class);

    private final KafkaTemplate<String, DocumentEvent> kafkaTemplate;
    private final IngestionKafkaProperties kafkaProperties;

    public DocumentEventProducer(
            KafkaTemplate<String, DocumentEvent> kafkaTemplate,
            IngestionKafkaProperties kafkaProperties
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaProperties = kafkaProperties;
    }

    public void publish(DocumentEvent event) {
        String topic = kafkaProperties.getRawDocumentEventsTopic();
        log.info(
                "Publishing raw document event documentId={} topic={} fileType={}",
                event.getDocumentId(),
                topic,
                event.getFileType()
        );

        try {
            SendResult<String, DocumentEvent> result = kafkaTemplate
                    .send(topic, event.getDocumentId(), event)
                    .get(kafkaProperties.getSendTimeoutSeconds(), TimeUnit.SECONDS);

            RecordMetadata metadata = result.getRecordMetadata();
            log.info(
                    "Kafka publish acknowledged documentId={} topic={} partition={} offset={}",
                    event.getDocumentId(),
                    metadata.topic(),
                    metadata.partition(),
                    metadata.offset()
            );
        } catch (TimeoutException e) {
            log.error(
                    "Kafka publish timed out after {}s documentId={} topic={}",
                    kafkaProperties.getSendTimeoutSeconds(),
                    event.getDocumentId(),
                    topic,
                    e
            );
            throw new DocumentEventPublishException("Timed out waiting for Kafka acknowledgment.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while publishing documentId={}", event.getDocumentId(), e);
            throw new DocumentEventPublishException("Interrupted while publishing to Kafka.", e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            log.error("Kafka publish failed documentId={} topic={}", event.getDocumentId(), topic, cause);
            throw new DocumentEventPublishException("Failed to publish document event to Kafka.", cause);
        }
    }
}
