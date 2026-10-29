package com.shopOnline.OrderService.service;

import com.shopOnline.OrderService.dto.OrderItemRequest;
import com.shopOnline.OrderService.dto.OrderItemResponse;
import java.util.List;
import java.util.UUID;

public interface OrderItemService {
    OrderItemResponse add(String orderId, OrderItemRequest request);
    List<OrderItemResponse> findByOrder(String orderId);
    OrderItemResponse update(UUID itemId, OrderItemRequest request);
    void delete(UUID itemId);
}
