package com.rag.chunking.config;

import com.rag.common.model.DocumentChunkedEvent;
import com.rag.common.model.DocumentTextExtractedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfig {

    private final ChunkingKafkaProperties kafkaProperties;
    private final KafkaProperties springKafkaProperties;

    public KafkaConfig(ChunkingKafkaProperties kafkaProperties, KafkaProperties springKafkaProperties) {
        this.kafkaProperties = kafkaProperties;
        this.springKafkaProperties = springKafkaProperties;
    }

    @Bean
    public ConsumerFactory<String, DocumentTextExtractedEvent> consumerFactory() {
        JsonDeserializer<DocumentTextExtractedEvent> deserializer =
                new JsonDeserializer<>(DocumentTextExtractedEvent.class);

        deserializer.addTrustedPackages(kafkaProperties.getTrustedPackages());

        Map<String, Object> props = new HashMap<>(springKafkaProperties.buildConsumerProperties(null));
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getGroupId());

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DocumentTextExtractedEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DocumentTextExtractedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ProducerFactory<String, DocumentChunkedEvent> chunkedEventProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, springKafkaProperties.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, DocumentChunkedEvent> chunkedEventKafkaTemplate(
            ProducerFactory<String, DocumentChunkedEvent> chunkedEventProducerFactory
    ) {
        return new KafkaTemplate<>(chunkedEventProducerFactory);
    }
}
