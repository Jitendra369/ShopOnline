package com.shoponline.payment_service.dto;

import com.shoponline.payment_service.enums.PaymentMethod;
import com.shoponline.payment_service.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String paymentId;
    private String orderId;
    private String customerId;
    private BigDecimal amount;
    private String currency;
    private PaymentMethod method;
    private PaymentStatus status;
    private String failureReason;
    private String transactionRef;
    private Instant createdAt;
    private Instant updatedAt;
}
