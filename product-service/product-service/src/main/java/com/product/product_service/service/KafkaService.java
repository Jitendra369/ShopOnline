package com.product.product_service.service;

import com.product.product_service.event.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic.product-created}")
    private String productCreatedTopic;

    public void sendProductCreatedEvent(ProductCreatedEvent event) {
        // keyed by productId so all events for a product land on the same partition in order
        kafkaTemplate.send(productCreatedTopic, event.getProductId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send event {}", event, ex);
                    } else {
                        log.info("Sent event {} to partition {} offset {}", event,
                                result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                    }
                });
    }
}
