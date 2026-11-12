package com.shoponline.payment_service.service;

import com.shoponline.payment_service.event.OrderCreatedEvent;
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
public class OrderEventListener {

    private final PaymentService paymentService;
    private final Validator validator;

    @KafkaListener(topics = "${app.kafka.topic.order}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "orderCreatedListenerContainerFactory")
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("Received event {}", event);
        Set<ConstraintViolation<OrderCreatedEvent>> violations = validator.validate(event);
        if (!violations.isEmpty())
            // not retryable, the error handler sends it straight to the DLT
            throw new ConstraintViolationException(violations);
        paymentService.payForOrder(event);
    }
}
