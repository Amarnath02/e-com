package com.spring.service.order;

import com.spring.Enum.OrderStatus;
import com.spring.dto.OrderDto;
import com.spring.entities.Cart;
import com.spring.entities.Order;
import com.spring.entities.OrderItem;
import com.spring.entities.Product;
import com.spring.repository.OrderRepository;
import com.spring.repository.ProductRepository;
import com.spring.service.cart.CartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

        private final OrderRepository orderRepository;
        private final ProductRepository productRepository;
        private final CartService cartService;
        private final ModelMapper modelMapper;


        @Transactional
        @Override
        public Order placeOrder(Long userId) {
            Cart cart   = cartService.getCartByUserId(userId);
            Order order = createOrder(cart);
            List<OrderItem> orderItemList = createOrderItems(order, cart);
            order.setOrderItems(new HashSet<>(orderItemList));
            order.setTotalAmount(calculateTotalAmount(orderItemList));
            Order savedOrder = orderRepository.save(order);
            cartService.clearCart(cart.getId());
            return savedOrder;
        }

        private Order createOrder(Cart cart) {
            Order order = new Order();
            order.setUser(cart.getUser());
            order.setOrderStatus(OrderStatus.PENDING);
            order.setOrderDate(LocalDate.now());
            return  order;
        }

        private List<OrderItem> createOrderItems(Order order, Cart cart) {
            return cart.getItems().stream().map(cartItem -> {
                Product product = cartItem.getProduct();
                product.setInventory(product.getInventory() - cartItem.getQuantity());
                productRepository.save(product);

                return new OrderItem(
                        null,
                        cartItem.getQuantity(),
                        cartItem.getUnitPrice(),
                        order,
                        product);
            }).toList();
        }

        private BigDecimal calculateTotalAmount(List<OrderItem> orderItemList) {
            return  orderItemList
                    .stream()
                    .map(item -> item.getPrice()
                            .multiply(new BigDecimal(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        @Override
        public OrderDto getOrder(Long orderId) {
            return orderRepository.findById(orderId)
                    .map(this::convertToDto)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        }


        @Override
        public List<OrderDto> getUserOrders(Long userId) {
            List<Order> orders = orderRepository.findByUserId(userId);
            return  orders.stream().map(order -> convertToDto(order)).toList();
        }

        private OrderDto convertToDto(Order order){
            return modelMapper.map(order, OrderDto.class);
        }
}
