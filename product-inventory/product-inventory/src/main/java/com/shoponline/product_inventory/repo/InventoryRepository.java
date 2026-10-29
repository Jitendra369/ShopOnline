package com.shoponline.product_inventory.repo;

import com.shoponline.product_inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductId(Long productId);

    @Modifying
    @Query("UPDATE Inventory i SET i.quantity = i.quantity - :qty WHERE i.productId = :productId AND i.quantity >= :qty")
    int reduceStock(@Param("productId") Long productId, @Param("qty") int qty);

    @Modifying
    @Query("UPDATE Inventory i SET i.quantity = i.quantity + :qty WHERE i.productId = :productId")
    int addStock(@Param("productId") Long productId, @Param("qty") int qty);
}
