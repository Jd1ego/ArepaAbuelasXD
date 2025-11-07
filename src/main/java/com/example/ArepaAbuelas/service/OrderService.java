package com.example.ArepaAbuelas.service;

import com.example.ArepaAbuelas.dto.OrderDTO;
import com.example.ArepaAbuelas.dto.OrderItemDTO;
import com.example.ArepaAbuelas.entity.Order;
import com.example.ArepaAbuelas.entity.OrderItem;
import com.example.ArepaAbuelas.entity.Product;
import com.example.ArepaAbuelas.entity.User;
import com.example.ArepaAbuelas.entity.Card;
import com.example.ArepaAbuelas.repository.OrderRepository;
import com.example.ArepaAbuelas.repository.ProductRepository;
import com.example.ArepaAbuelas.repository.UserRepository;
import com.example.ArepaAbuelas.repository.CardRepository;
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

        Optional<User> user = userRepository.findById(dto.getUserId());
        if (user.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + dto.getUserId());
        }

        order.setUser(user.get());
        order.setDate(LocalDateTime.now());

        // ✅ Tarjeta simulada — se usa el número cifrado ya guardado
        // Se busca la tarjeta por los 4 últimos dígitos, o simplemente se toma el número del DTO para simular pago
        Card card = cardRepository.findByUserId(user.get().getId())
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No saved card found for this user."));

        order.setCardNumber(card.getCardNumberEncrypted() != null ? card.getCardNumberEncrypted() : dto.getCardNumber());
        order.setExpiry(card.getExpiry());
        order.setCvv(card.getCvvEncrypted() != null ? card.getCvvEncrypted() : dto.getCvv());

        // 🛒 Procesar ítems
        List<OrderItem> items = dto.getItems().stream().map(itemDto -> {
            OrderItem item = new OrderItem();
            Optional<Product> product = productRepository.findById(itemDto.getProductId());
            product.ifPresent(item::setProduct);
            item.setQuantity(itemDto.getQuantity());
            return item;
        }).collect(Collectors.toList());

        order.setItems(items);

        // 💰 Calcular total
        final double[] total = {items.stream()
                .mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity())
                .sum()};

        // 🎟️ Aplicar cupón si existe
        if (couponCode != null && !couponCode.isBlank()) {
            couponService.applyCoupon(order.getUser(), couponCode).ifPresent(discount -> {
                total[0] -= total[0] * discount;
            });
        }

        order.setTotal(total[0]);

        // 💾 Guardar orden
        order = orderRepository.save(order);

        // 📦 Retornar DTO
        dto.setId(order.getId());
        dto.setDate(order.getDate());
        dto.setTotal(order.getTotal());

        // Por seguridad, no devolvemos datos de tarjeta
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
