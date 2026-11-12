package com.shoponline.payment_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 * Stands in for a real payment provider. Two rules, both set in application.properties:
 *   1. amount above app.payment.max-amount  -> always fails (predictable, to test the failure path on purpose)
 *   2. otherwise, app.payment.failure-rate   -> share of payments that fail at random
 */
@Component
public class PaymentSimulator {

    public record Result(boolean success, String failureReason) {
        static Result ok() {
            return new Result(true, null);
        }

        static Result failed(String reason) {
            return new Result(false, reason);
        }
    }

    private final BigDecimal maxAmount;
    private final double failureRate;
    private final Supplier<Random> random;

    public PaymentSimulator(@Value("${app.payment.max-amount}") BigDecimal maxAmount,
                            @Value("${app.payment.failure-rate}") double failureRate) {
        this(maxAmount, failureRate, ThreadLocalRandom::current);
    }

    // lets a test pass a seeded Random
    PaymentSimulator(BigDecimal maxAmount, double failureRate, Supplier<Random> random) {
        if (failureRate < 0 || failureRate > 1)
            throw new IllegalArgumentException("app.payment.failure-rate must be between 0.0 and 1.0");
        this.maxAmount = maxAmount;
        this.failureRate = failureRate;
        this.random = random;
    }

    public Result charge(BigDecimal amount) {
        if (amount.compareTo(maxAmount) > 0)
            return Result.failed("Amount " + amount + " exceeds the limit of " + maxAmount);
        if (random.get().nextDouble() < failureRate)
            return Result.failed("Declined by the payment provider (simulated)");
        return Result.ok();
    }
}
