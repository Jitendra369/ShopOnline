package com.shoponline.product_inventory.config;

import com.shoponline.product_inventory.dto.InventoryEvent;
import com.shoponline.product_inventory.event.ProductCreatedEvent;
import com.shoponline.product_inventory.exception.ProductNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${app.kafka.topic.inventory}")
    private String inventoryTopic;

    @Value("${app.kafka.topic.product-created}")
    private String productCreatedTopic;

    @Bean
    public ProducerFactory<String, Object> producerFactory(KafkaProperties kafkaProperties) {
        return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties());
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        KafkaTemplate<String, Object> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        kafkaTemplate.setDefaultTopic(inventoryTopic);
        return kafkaTemplate;
    }

    @Bean
    public ConsumerFactory<String, InventoryEvent> consumerFactory(KafkaProperties kafkaProperties) {
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InventoryEvent> kafkaListenerContainerFactory(
            ConsumerFactory<String, InventoryEvent> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, InventoryEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductCreatedEvent> productCreatedListenerContainerFactory(
            KafkaProperties kafkaProperties, KafkaTemplate<String, Object> kafkaTemplate) {
        // same consumer settings as the default factory, but deserializing into ProductCreatedEvent
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        props.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE, ProductCreatedEvent.class.getName());
        props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, ProductCreatedEvent.class.getPackageName());
        props.put(JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        // retry transient failures twice, then publish the record to <topic>.DLT
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(kafkaTemplate), new FixedBackOff(1000L, 2));
        errorHandler.addNotRetryableExceptions(ConstraintViolationException.class, ProductNotFoundException.class);

        ConcurrentKafkaListenerContainerFactory<String, ProductCreatedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(props));
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    @Bean
    public NewTopic inventoryTopic() {
        return TopicBuilder.name(inventoryTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic productCreatedDltTopic() {
        // partition count must match the source topic, the recoverer publishes to the same partition
        return TopicBuilder.name(productCreatedTopic + ".DLT")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
