package com.shoponline.payment_service.event;

import com.shoponline.payment_service.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Event Order Service will publish on the order-events topic, keyed by orderId.
 * Order Service doesn't publish it yet; this class is the contract it should follow.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreatedEvent {
    @NotBlank
    private String eventId;
    private String eventType;
    private String source;
    private Instant createdAt;
    @NotBlank
    private String orderId;
    private String customerId;
    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal totalAmount;
    // optional, default INR
    private String currency;
    // optional, default UPI
    private PaymentMethod paymentMethod;
}
