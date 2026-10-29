package com.shopOnline.OrderService.service;

import com.shopOnline.OrderService.dto.OrderItemRequest;
import com.shopOnline.OrderService.dto.OrderItemResponse;
import com.shopOnline.OrderService.entity.Order;
import com.shopOnline.OrderService.entity.OrderItem;
import com.shopOnline.OrderService.exception.ResourceNotFoundException;
import com.shopOnline.OrderService.repository.OrderItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository itemRepository;
    private final OrderServiceImpl orderService;

    @Override
    public OrderItemResponse add(String orderId, OrderItemRequest request) {
        Order order = orderService.getOrder(orderId);
        OrderItem item = OrderServiceImpl.toEntity(request);
        order.addItem(item);
        OrderServiceImpl.recalculate(order);
        return OrderServiceImpl.toItemResponse(itemRepository.save(item));
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<OrderItemResponse> findByOrder(String orderId) {
        orderService.getOrder(orderId);
        return itemRepository.findByOrder_OrderId(orderId).stream().map(OrderServiceImpl::toItemResponse).toList();
    }

    @Override
    public OrderItemResponse update(UUID itemId, OrderItemRequest request) {
        OrderItem item = getItem(itemId);
        Order order = item.getOrder();
        OrderItem updated = OrderServiceImpl.toEntity(request);
        item.setProductId(updated.getProductId());
        item.setQuantity(updated.getQuantity());
        item.setUnitPrice(updated.getUnitPrice());
        OrderServiceImpl.recalculate(order);
        return OrderServiceImpl.toItemResponse(itemRepository.save(item));
    }

    @Override
    public void delete(UUID itemId) {
        OrderItem item = getItem(itemId);
        Order order = item.getOrder();
        order.removeItem(item);
        OrderServiceImpl.recalculate(order);
        itemRepository.delete(item);
    }

    private OrderItem getItem(UUID id) {
        return itemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order item not found: " + id));
    }
}
