package com.spring.service.order;

import com.spring.dto.OrderDto;
import com.spring.dto.OrderItemDto;
import com.spring.entities.Order;

import java.util.List;

public interface OrderService {
    Order placeOrder(Long userId);
    OrderDto getOrder(Long orderId);
    List<OrderDto> getUserOrders(Long userId);
}
