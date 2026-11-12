package com.shoponline.payment_service.controller;

import com.shoponline.payment_service.dto.PaymentRequest;
import com.shoponline.payment_service.dto.PaymentResponse;
import com.shoponline.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    // 201 with the new payment (status COMPLETED or FAILED),
    // 200 with the existing payment if this order was already paid (safe to retry)
    @PostMapping
    public ResponseEntity<PaymentResponse> pay(@Valid @RequestBody PaymentRequest request) {
        PaymentService.PaymentResult result = paymentService.pay(request);
        return new ResponseEntity<>(result.payment(), result.created() ? HttpStatus.CREATED : HttpStatus.OK);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getByPaymentId(@PathVariable String paymentId) {
        return ResponseEntity.ok(paymentService.getByPaymentId(paymentId));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getByOrderId(@PathVariable String orderId) {
        return ResponseEntity.ok(paymentService.getByOrderId(orderId));
    }

    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<PaymentResponse> refund(@PathVariable String paymentId) {
        return ResponseEntity.ok(paymentService.refund(paymentId));
    }
}
