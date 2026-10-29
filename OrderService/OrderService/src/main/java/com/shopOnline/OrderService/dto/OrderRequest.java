package com.shopOnline.OrderService.dto;

import com.shopOnline.OrderService.entity.OrderStatus;
import java.util.List;

public record OrderRequest(OrderStatus status, List<OrderItemRequest> items) { }
