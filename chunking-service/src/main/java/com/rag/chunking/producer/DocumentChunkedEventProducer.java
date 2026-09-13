package com.rag.chunking.producer;

import com.rag.chunking.config.ChunkingKafkaProperties;
import com.rag.chunking.exception.DocumentChunkingException;
import com.rag.common.model.DocumentChunkedEvent;
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
public class DocumentChunkedEventProducer {

    private static final Logger log = LoggerFactory.getLogger(DocumentChunkedEventProducer.class);

    private final KafkaTemplate<String, DocumentChunkedEvent> kafkaTemplate;
    private final ChunkingKafkaProperties kafkaProperties;

    public DocumentChunkedEventProducer(
            KafkaTemplate<String, DocumentChunkedEvent> kafkaTemplate,
            ChunkingKafkaProperties kafkaProperties
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaProperties = kafkaProperties;
    }

    public void publish(DocumentChunkedEvent event) {
        String topic = kafkaProperties.getDocumentChunkedEventsTopic();
        log.info(
                "Publishing chunked document event documentId={} topic={} chunks={} strategy={}",
                event.getDocumentId(),
                topic,
                event.getChunkCount(),
                event.getChunkingStrategy()
        );

        try {
            SendResult<String, DocumentChunkedEvent> result = kafkaTemplate
                    .send(topic, event.getDocumentId(), event)
                    .get(kafkaProperties.getSendTimeoutSeconds(), TimeUnit.SECONDS);

            RecordMetadata metadata = result.getRecordMetadata();
            log.info(
                    "Chunked document event acknowledged documentId={} topic={} partition={} offset={}",
                    event.getDocumentId(),
                    metadata.topic(),
                    metadata.partition(),
                    metadata.offset()
            );
        } catch (TimeoutException e) {
            log.error(
                    "Chunked document event publish timed out after {}s documentId={} topic={}",
                    kafkaProperties.getSendTimeoutSeconds(),
                    event.getDocumentId(),
                    topic,
                    e
            );
            throw new DocumentChunkingException("Timed out publishing chunked document event.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while publishing chunked document event documentId={}", event.getDocumentId(), e);
            throw new DocumentChunkingException("Interrupted while publishing chunked document event.", e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            log.error("Failed to publish chunked document event documentId={} topic={}", event.getDocumentId(), topic, cause);
            throw new DocumentChunkingException("Failed to publish chunked document event.", cause);
        }
    }
}
