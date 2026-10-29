package com.shoponline.product_inventory.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreatedEvent {
    @NotBlank
    private String eventId;
    private String eventType;
    private String source;
    private Instant createdAt;
    @NotNull
    private Long productId;
    @NotNull
    @PositiveOrZero
    private Integer quantity;
}
