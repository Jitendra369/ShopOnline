package com.shopOnline.OrderService.service;

import com.shopOnline.OrderService.dto.OrderItemRequest;
import com.shopOnline.OrderService.dto.OrderItemResponse;
import com.shopOnline.OrderService.dto.OrderRequest;
import com.shopOnline.OrderService.dto.OrderResponse;
import com.shopOnline.OrderService.entity.Order;
import com.shopOnline.OrderService.entity.OrderItem;
import com.shopOnline.OrderService.entity.OrderStatus;
import com.shopOnline.OrderService.exception.InvalidRequestException;
import com.shopOnline.OrderService.exception.ResourceNotFoundException;
import com.shopOnline.OrderService.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    @Override
    public OrderResponse create(OrderRequest request) {
        Order order = new Order();
        order.setStatus(request != null && request.status() != null ? request.status() : OrderStatus.CREATED);
        if (request != null && request.items() != null) {
            request.items().forEach(item -> order.addItem(toEntity(item)));
        }
        recalculate(order);
        return toResponse(orderRepository.save(order));
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<OrderResponse> findAll() {
        return orderRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public OrderResponse findByOrderId(String orderId) { return toResponse(getOrder(orderId)); }

    @Override
    public OrderResponse update(String orderId, OrderRequest request) {
        if (request == null) throw new InvalidRequestException("Request body is required");
        Order order = getOrder(orderId);
        if (request.status() != null) order.setStatus(request.status());
        if (request.items() != null) {
            order.getItems().clear();
            request.items().forEach(item -> order.addItem(toEntity(item)));
        }
        recalculate(order);
        return toResponse(orderRepository.save(order));
    }

    @Override
    public void delete(String orderId) { orderRepository.delete(getOrder(orderId)); }

    Order getOrder(String orderId) {
        return orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
    }

    static OrderItem toEntity(OrderItemRequest request) {
        if (request == null || request.productId() == null || request.productId().isBlank())
            throw new InvalidRequestException("productId is required");
        if (request.quantity() == null || request.quantity() <= 0)
            throw new InvalidRequestException("quantity must be greater than zero");
        if (request.unitPrice() == null || request.unitPrice().signum() < 0)
            throw new InvalidRequestException("unitPrice must be zero or greater");
        OrderItem item = new OrderItem();
        item.setProductId(request.productId());
        item.setQuantity(request.quantity());
        item.setUnitPrice(request.unitPrice());
        return item;
    }

    static void recalculate(Order order) {
        order.setTotalAmount(order.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    static OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(item.getId(), item.getProductId(), item.getQuantity(), item.getUnitPrice());
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(order.getOrderId(), order.getTotalAmount(), order.getStatus(), order.getOrderDate(),
                order.getItems().stream().map(OrderServiceImpl::toItemResponse).toList());
    }
}
