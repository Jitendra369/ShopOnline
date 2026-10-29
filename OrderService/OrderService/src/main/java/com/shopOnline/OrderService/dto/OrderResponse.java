package com.shopOnline.OrderService.dto;

import com.shopOnline.OrderService.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(String orderId, BigDecimal totalAmount, OrderStatus status,
                            LocalDateTime orderDate, List<OrderItemResponse> items) { }
