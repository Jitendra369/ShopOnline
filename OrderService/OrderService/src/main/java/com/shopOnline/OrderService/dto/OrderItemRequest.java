package com.shopOnline.OrderService.dto;

import java.math.BigDecimal;

public record OrderItemRequest(String productId, Integer quantity, BigDecimal unitPrice) { }
