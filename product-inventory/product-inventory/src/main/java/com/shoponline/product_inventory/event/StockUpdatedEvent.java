package com.shoponline.product_inventory.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockUpdatedEvent {
    private Long productId;
    private Integer quantity; // current total stock
}
