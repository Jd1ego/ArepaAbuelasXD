package com.example.ArepaAbuelas.service;

import com.example.ArepaAbuelas.dto.OrderDTO;
import com.example.ArepaAbuelas.dto.OrderItemDTO;
import com.example.ArepaAbuelas.entity.Order;
import com.example.ArepaAbuelas.entity.OrderItem;
import com.example.ArepaAbuelas.entity.Product;
import com.example.ArepaAbuelas.entity.User;
import com.example.ArepaAbuelas.repository.OrderRepository;
import com.example.ArepaAbuelas.repository.ProductRepository;
import com.example.ArepaAbuelas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CouponService couponService;

    public OrderDTO createOrder(OrderDTO dto, String couponCode) {
        Order order = new Order();
        Optional<User> user = userRepository.findById(dto.getUserId());
        if (user.isEmpty()) return null;

        order.setUser(user.get());
        order.setDate(LocalDateTime.now());

        // Simulate card storage (DO NOT USE REAL DATA)
        order.setCardNumber(dto.getCardNumber()); // In real: encrypt
        order.setExpiry(dto.getExpiry());
        order.setCvv(dto.getCvv());

        List<OrderItem> items = dto.getItems().stream().map(itemDto -> {
            OrderItem item = new OrderItem();
            Optional<Product> product = productRepository.findById(itemDto.getProductId());
            product.ifPresent(item::setProduct);
            item.setQuantity(itemDto.getQuantity());
            return item;
        }).collect(Collectors.toList());

        order.setItems(items);

        final double[] total = {items.stream().mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity()).sum()};

        // Apply coupon if valid
        if (couponCode != null) {
            couponService.applyCoupon(order.getUser(), couponCode).ifPresent(discount -> {
                total[0] -= total[0] * discount;
            });
        }

        order.setTotal(total[0]);
        order = orderRepository.save(order);
        dto.setId(order.getId());
        dto.setDate(order.getDate());
        dto.setTotal(order.getTotal());
        return dto;
    }

    public List<OrderDTO> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId).stream().map(o -> {
            OrderDTO dto = new OrderDTO();
            dto.setId(o.getId());
            dto.setUserId(o.getUser().getId());
            dto.setDate(o.getDate());
            dto.setTotal(o.getTotal());
            // Omit card details for security
            dto.setItems(o.getItems().stream().map(i -> {
                OrderItemDTO itemDto = new OrderItemDTO();
                itemDto.setProductId(i.getProduct().getId());
                itemDto.setQuantity(i.getQuantity());
                return itemDto;
            }).collect(Collectors.toList()));
            return dto;
        }).collect(Collectors.toList());
    }
}