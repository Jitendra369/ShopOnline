package com.shopOnline.OrderService.repository;

import com.shopOnline.OrderService.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByOrderId(String orderId);
    boolean existsByOrderId(String orderId);
    void deleteByOrderId(String orderId);
}
