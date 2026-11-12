package com.shoponline.payment_service.config;

import com.shoponline.payment_service.event.OrderCreatedEvent;
import jakarta.validation.ConstraintViolationException;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
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

    @Value("${app.kafka.topic.order}")
    private String orderTopic;

    @Value("${app.kafka.topic.payment}")
    private String paymentTopic;

    @Bean
    public ProducerFactory<String, Object> producerFactory(KafkaProperties kafkaProperties) {
        return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties());
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        KafkaTemplate<String, Object> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        kafkaTemplate.setDefaultTopic(paymentTopic);
        return kafkaTemplate;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent> orderCreatedListenerContainerFactory(
            KafkaProperties kafkaProperties, KafkaTemplate<String, Object> kafkaTemplate) {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());
        props.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE, OrderCreatedEvent.class.getName());
        props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, OrderCreatedEvent.class.getPackageName());
        props.put(JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        // retry transient failures (e.g. database down) twice, then publish the record to <topic>.DLT
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(kafkaTemplate), new FixedBackOff(1000L, 2));
        errorHandler.addNotRetryableExceptions(ConstraintViolationException.class);

        ConcurrentKafkaListenerContainerFactory<String, OrderCreatedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(props));
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    @Bean
    public NewTopic paymentTopic() {
        return TopicBuilder.name(paymentTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderTopic() {
        // created here too so the listener can start before Order Service publishes anything
        return TopicBuilder.name(orderTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderDltTopic() {
        // partition count must match the source topic, the recoverer publishes to the same partition
        return TopicBuilder.name(orderTopic + ".DLT")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
