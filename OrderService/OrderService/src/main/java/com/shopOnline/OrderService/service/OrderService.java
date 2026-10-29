package com.shopOnline.OrderService.service;

import com.shopOnline.OrderService.dto.OrderRequest;
import com.shopOnline.OrderService.dto.OrderResponse;
import java.util.List;

public interface OrderService {
    OrderResponse create(OrderRequest request);
    List<OrderResponse> findAll();
    OrderResponse findByOrderId(String orderId);
    OrderResponse update(String orderId, OrderRequest request);
    void delete(String orderId);
}
