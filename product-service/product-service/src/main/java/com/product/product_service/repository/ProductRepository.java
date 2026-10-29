package com.product.product_service.repository;

import com.product.product_service.entity.ProductEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<ProductEntity,Long> {

    Optional<ProductEntity> findBySku(String sku);

    List<ProductEntity> findByCategoryAndActiveTrue(String category);

//    These repository methods let you fetch root categories, their direct children, and products belonging to a specific category.
    List<ProductEntity> findByCategoryIdAndActiveTrue(String categoryId);
}
