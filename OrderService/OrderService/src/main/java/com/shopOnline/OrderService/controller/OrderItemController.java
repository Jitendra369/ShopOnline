package com.shopOnline.OrderService.controller;

import com.shopOnline.OrderService.dto.OrderItemRequest;
import com.shopOnline.OrderService.dto.OrderItemResponse;
import com.shopOnline.OrderService.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderItemController {
    private final OrderItemService itemService;

    @PostMapping("/orders/{orderId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderItemResponse add(@PathVariable String orderId, @RequestBody OrderItemRequest request) {
        return itemService.add(orderId, request);
    }

    @GetMapping("/orders/{orderId}/items")
    public List<OrderItemResponse> findByOrder(@PathVariable String orderId) { return itemService.findByOrder(orderId); }

    @PutMapping("/order-items/{itemId}")
    public OrderItemResponse update(@PathVariable UUID itemId, @RequestBody OrderItemRequest request) {
        return itemService.update(itemId, request);
    }

    @DeleteMapping("/order-items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID itemId) { itemService.delete(itemId); }
}
