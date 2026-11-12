package com.shoponline.payment_service.service;

import com.shoponline.payment_service.dto.PaymentRequest;
import com.shoponline.payment_service.dto.PaymentResponse;
import com.shoponline.payment_service.entity.Payment;
import com.shoponline.payment_service.enums.PaymentMethod;
import com.shoponline.payment_service.enums.PaymentStatus;
import com.shoponline.payment_service.event.OrderCreatedEvent;
import com.shoponline.payment_service.event.PaymentEvent;
import com.shoponline.payment_service.event.PaymentEventType;
import com.shoponline.payment_service.exception.InvalidPaymentStateException;
import com.shoponline.payment_service.exception.ResourceNotFoundException;
import com.shoponline.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final String DEFAULT_CURRENCY = "INR";
    private static final PaymentMethod DEFAULT_METHOD = PaymentMethod.UPI;

    private final PaymentRepository repo;
    private final PaymentSimulator simulator;
    private final KafkaService kafkaService;

    // created = false when the order was already paid before and the existing payment is returned
    public record PaymentResult(PaymentResponse payment, boolean created) {
    }

    public PaymentResult pay(PaymentRequest req) {
        return process(req.getOrderId(), req.getCustomerId(), req.getAmount(), req.getCurrency(), req.getMethod());
    }

    public void payForOrder(OrderCreatedEvent event) {
        PaymentResult result = process(event.getOrderId(), event.getCustomerId(), event.getTotalAmount(),
                event.getCurrency(), event.getPaymentMethod());
        if (!result.created())
            log.info("Order {} already has payment {}, skipping duplicate event {}",
                    event.getOrderId(), result.payment().getPaymentId(), event.getEventId());
    }

    // Not @Transactional on purpose: save() commits on its own, so a duplicate-key error on a concurrent
    // insert can be caught and answered with the existing payment instead of failing the request
    private PaymentResult process(String orderId, String customerId, BigDecimal amount,
                                  String currency, PaymentMethod method) {
        // Kafka delivers at least once and users double-click, so one order must never be charged twice
        var existing = repo.findByOrderId(orderId);
        if (existing.isPresent())
            return new PaymentResult(mapToDto(existing.get()), false);

        PaymentSimulator.Result outcome = simulator.charge(amount);

        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID().toString());
        payment.setOrderId(orderId);
        payment.setCustomerId(customerId);
        payment.setAmount(amount);
        payment.setCurrency(currency != null ? currency : DEFAULT_CURRENCY);
        payment.setMethod(method != null ? method : DEFAULT_METHOD);
        if (outcome.success()) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setTransactionRef("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(outcome.failureReason());
        }

        Payment saved;
        try {
            saved = repo.save(payment);
        } catch (DataIntegrityViolationException e) {
            // a concurrent request for the same order inserted its payment between the check and the insert
            return repo.findByOrderId(orderId)
                    .map(p -> new PaymentResult(mapToDto(p), false))
                    .orElseThrow(() -> e);
        }

        log.info("Payment {} for order {}: {}", saved.getPaymentId(), orderId, saved.getStatus());
        publish(saved, saved.getStatus() == PaymentStatus.COMPLETED
                ? PaymentEventType.PAYMENT_COMPLETED : PaymentEventType.PAYMENT_FAILED);
        return new PaymentResult(mapToDto(saved), true);
    }

    @Transactional
    public PaymentResponse refund(String paymentId) {
        Payment payment = findPayment(paymentId);
        if (payment.getStatus() != PaymentStatus.COMPLETED)
            throw new InvalidPaymentStateException(
                    "Only COMPLETED payments can be refunded, payment " + paymentId + " is " + payment.getStatus());

        payment.setStatus(PaymentStatus.REFUNDED);
        publish(payment, PaymentEventType.PAYMENT_REFUNDED);   // sent after commit
        return mapToDto(payment);
    }

    public PaymentResponse getByPaymentId(String paymentId) {
        return mapToDto(findPayment(paymentId));
    }

    public PaymentResponse getByOrderId(String orderId) {
        return repo.findByOrderId(orderId)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "orderId", orderId));
    }

    private Payment findPayment(String paymentId) {
        return repo.findByPaymentId(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "paymentId", paymentId));
    }

    private void publish(Payment payment, PaymentEventType eventType) {
        kafkaService.sendEvent(new PaymentEvent(
                UUID.randomUUID().toString(),
                eventType,
                payment.getOrderId(),
                payment.getPaymentId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getFailureReason(),
                Instant.now()));
    }

    private PaymentResponse mapToDto(Payment payment) {
        return new PaymentResponse(
                payment.getPaymentId(),
                payment.getOrderId(),
                payment.getCustomerId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getMethod(),
                payment.getStatus(),
                payment.getFailureReason(),
                payment.getTransactionRef(),
                payment.getCreatedAt(),
                payment.getUpdatedAt());
    }
}
