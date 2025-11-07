package com.example.ArepaAbuelas.service;

import com.example.ArepaAbuelas.dto.OrderDTO;
import com.example.ArepaAbuelas.dto.OrderItemDTO;
import com.example.ArepaAbuelas.entity.Card;
import com.example.ArepaAbuelas.entity.Order;
import com.example.ArepaAbuelas.entity.OrderItem;
import com.example.ArepaAbuelas.entity.Product;
import com.example.ArepaAbuelas.entity.User;
import com.example.ArepaAbuelas.repository.CardRepository;
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

    @Autowired
    private CardRepository cardRepository;

    public OrderDTO createOrder(OrderDTO dto, String couponCode) {
        Order order = new Order();

        // 1️⃣ Obtener usuario
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + dto.getUserId()));
        order.setUser(user);
        order.setDate(LocalDateTime.now());

        // 2️⃣ Obtener tarjeta del usuario
        List<Card> cards = cardRepository.findByUserId(user.getId());
        if (cards.isEmpty()) {
            throw new RuntimeException("No saved card found for this user.");
        }
        Card card = cards.get(0); // Tomamos la primera tarjeta

        // Debug opcional
        System.out.println("User ID: " + user.getId());
        System.out.println("Using card: " + card.getLast4() + " (" + card.getCardHolder() + ")");

        // 3️⃣ Asignar datos de tarjeta (usando fallback del DTO si los cifrados están vacíos)
        order.setCardNumber(card.getCardNumberEncrypted() != null ? card.getCardNumberEncrypted() : dto.getCardNumber());
        order.setExpiry(card.getExpiry() != null ? card.getExpiry() : dto.getExpiry());
        order.setCvv(card.getCvvEncrypted() != null ? card.getCvvEncrypted() : dto.getCvv());

        // 4️⃣ Procesar ítems
        List<OrderItem> items = dto.getItems().stream().map(itemDto -> {
            OrderItem item = new OrderItem();
            Optional<Product> product = productRepository.findById(itemDto.getProductId());
            product.ifPresent(item::setProduct);
            item.setQuantity(itemDto.getQuantity());
            return item;
        }).collect(Collectors.toList());

        order.setItems(items);

        // 5️⃣ Calcular total
        final double[] total = {items.stream()
                .mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity())
                .sum()};

        // 6️⃣ Aplicar cupón si existe
        if (couponCode != null && !couponCode.isBlank()) {
            couponService.applyCoupon(user, couponCode).ifPresent(discount -> {
                total[0] -= total[0] * discount;
            });
        }
        order.setTotal(total[0]);

        // 7️⃣ Guardar orden
        order = orderRepository.save(order);

        // 8️⃣ Construir DTO de respuesta
        dto.setId(order.getId());
        dto.setDate(order.getDate());
        dto.setTotal(order.getTotal());

        // No devolvemos datos sensibles de tarjeta
        dto.setCardNumber(null);
        dto.setCvv(null);
        dto.setExpiry(card.getExpiry());

        return dto;
    }

    public List<OrderDTO> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId).stream().map(o -> {
            OrderDTO dto = new OrderDTO();
            dto.setId(o.getId());
            dto.setUserId(o.getUser().getId());
            dto.setDate(o.getDate());
            dto.setTotal(o.getTotal());

            // No incluir datos de tarjeta
            dto.setCardNumber(null);
            dto.setCvv(null);
            dto.setExpiry(null);

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
