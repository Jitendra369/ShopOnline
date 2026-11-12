package com.shoponline.payment_service.exception;

// thrown when an action doesn't fit the payment's current status, e.g. refunding a FAILED payment
public class InvalidPaymentStateException extends RuntimeException {

    public InvalidPaymentStateException(String message) {
        super(message);
    }
}
