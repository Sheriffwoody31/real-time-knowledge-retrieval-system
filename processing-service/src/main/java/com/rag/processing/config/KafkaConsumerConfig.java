package com.rag.processing.config;

import com.rag.common.model.DocumentEvent;
import com.rag.common.model.DocumentTextExtractedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.context.annotation.Bean;
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
public class KafkaConsumerConfig {

    private final ProcessingKafkaProperties kafkaProperties;
    private final KafkaProperties springKafkaProperties;

    public KafkaConsumerConfig(ProcessingKafkaProperties kafkaProperties, KafkaProperties springKafkaProperties) {
        this.kafkaProperties = kafkaProperties;
        this.springKafkaProperties = springKafkaProperties;
    }

    @Bean
    public ConsumerFactory<String, DocumentEvent> consumerFactory() {
        JsonDeserializer<DocumentEvent> deserializer =
                new JsonDeserializer<>(DocumentEvent.class);

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
    public ConcurrentKafkaListenerContainerFactory<String, DocumentEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DocumentEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ProducerFactory<String, DocumentTextExtractedEvent> extractedEventProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, springKafkaProperties.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, DocumentTextExtractedEvent> extractedEventKafkaTemplate(
            ProducerFactory<String, DocumentTextExtractedEvent> extractedEventProducerFactory
    ) {
        return new KafkaTemplate<>(extractedEventProducerFactory);
    }
}
