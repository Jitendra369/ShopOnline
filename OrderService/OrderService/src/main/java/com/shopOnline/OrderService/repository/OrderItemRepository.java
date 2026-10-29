package com.shopOnline.OrderService.repository;

import com.shopOnline.OrderService.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    List<OrderItem> findByOrder_OrderId(String orderId);
}
