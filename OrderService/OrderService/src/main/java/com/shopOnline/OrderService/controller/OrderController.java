package com.shopOnline.OrderService.controller;

import com.shopOnline.OrderService.dto.OrderRequest;
import com.shopOnline.OrderService.dto.OrderResponse;
import com.shopOnline.OrderService.service.OrderService;
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

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@RequestBody(required = false) OrderRequest request) { return orderService.create(request); }

    @GetMapping
    public List<OrderResponse> findAll() { return orderService.findAll(); }

    @GetMapping("/{orderId}")
    public OrderResponse findById(@PathVariable String orderId) { return orderService.findByOrderId(orderId); }

    @PutMapping("/{orderId}")
    public OrderResponse update(@PathVariable String orderId, @RequestBody OrderRequest request) {
        return orderService.update(orderId, request);
    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String orderId) { orderService.delete(orderId); }
}
