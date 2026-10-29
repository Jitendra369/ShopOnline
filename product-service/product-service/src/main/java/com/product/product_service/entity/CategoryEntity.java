package com.product.product_service.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "categories")
public class CategoryEntity extends BaseEntity{

    private String name;
    private String categoryCode;
    private String slug;
    private String parentId;
    private String description;
    private boolean active = true;
}
