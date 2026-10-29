package com.product.product_service.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@Document(collection = "products")
public class ProductEntity extends BaseEntity{

    private String name;
    private String sku;
    private String description;
    private BigDecimal price;
    private String currency;
    private String category;
    private Integer stockInQuantity;
    private List<String> imageUrl;
    private boolean active;
    private String categoryId;
}
