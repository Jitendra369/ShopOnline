package com.product.product_service.repository;

import com.product.product_service.entity.CategoryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CategoryRepository extends MongoRepository<CategoryEntity,Long> {
    List<CategoryEntity> findByActiveTrue();
    List<CategoryEntity> findByParentIdAndActiveTrue(String parentId);
    List<CategoryEntity> findByParentIdIsNullAndActiveTrue();
}
