package com.shoponline.payment_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Published on the payment-events topic, keyed by orderId.
 * Order Service confirms or cancels the order from it; Inventory releases stock on PAYMENT_FAILED.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {
    private String eventId;
    private PaymentEventType eventType;
    private String orderId;
    private String paymentId;
    private BigDecimal amount;
    private String currency;
    private String reason;   // set only for PAYMENT_FAILED
    private Instant timestamp;
}
