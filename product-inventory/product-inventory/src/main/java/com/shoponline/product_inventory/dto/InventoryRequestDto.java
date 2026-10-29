package com.shoponline.product_inventory.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryRequestDto {
    @NotNull
    private Long productId;

    @NotNull
    @PositiveOrZero
    private Integer quantity;
}
