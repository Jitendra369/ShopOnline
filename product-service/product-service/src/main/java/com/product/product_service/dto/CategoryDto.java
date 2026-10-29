package com.product.product_service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class CategoryDto {
    private Long id;
    private String name;
    private String categoryCode;
    private String slug;
    private String parentId;
    private String description;
    private boolean active;
    private LocalDateTime createdDate;
    private LocalDateTime updatedAt;
}
