package com.shoponline.payment_service.dto;

import com.shoponline.payment_service.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    @NotBlank
    private String orderId;

    private String customerId;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    // ISO code, defaults to INR when not sent
    @Pattern(regexp = "[A-Z]{3}", message = "must be a 3-letter currency code, e.g. INR")
    private String currency;

    @NotNull
    private PaymentMethod method;
}
