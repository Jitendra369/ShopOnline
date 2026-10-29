package com.shoponline.product_inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryEvent {
    private Long productId;
    private Integer quantity;
    private InventoryEventType eventType;
    private Instant timestamp;
}
