package com.product.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;
    private String name;
    private String sku;
    private String description;
    private BigDecimal price;
    private String currency;
    private String category;
    private String categoryId;
    private Integer stockInQuantity;
    private List<String> imageUrl;
    private boolean active;
}
