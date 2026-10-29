package com.product.product_service.event;

import com.product.product_service.consts.ProductConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreatedEvent {
    private String eventId;
    private String eventType;
    private String source;
    private Instant createdAt;
    private Long productId;
    private Integer quantity;

    public static ProductCreatedEvent of(Long productId, Integer quantity) {
        return new ProductCreatedEvent(
                UUID.randomUUID().toString(), ProductConstant.PRODUCT_CREATED, "product-service", Instant.now(), productId, quantity);
    }
}
