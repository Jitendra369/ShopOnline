package com.shoponline.product_inventory.service;

import com.shoponline.product_inventory.event.ProductCreatedEvent;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductEventListener {

    private final InventoryService inventoryService;
    private final Validator validator;

    @KafkaListener(topics = "${app.kafka.topic.product-created}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "productCreatedListenerContainerFactory")
    public void onProductCreated(ProductCreatedEvent event) {
        log.info("Received event {}", event);
        Set<ConstraintViolation<ProductCreatedEvent>> violations = validator.validate(event);
        if (!violations.isEmpty())
            // not retryable, the error handler sends it straight to the DLT
            throw new ConstraintViolationException(violations);
        inventoryService.createFromProductEvent(event);
    }
}
