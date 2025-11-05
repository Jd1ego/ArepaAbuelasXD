package com.example.ArepaAbuelas.controller;
// src/main/java/com/arepabuelas/controller/OrderController.java


import com.example.ArepaAbuelas.dto.OrderDTO;
import com.example.ArepaAbuelas.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO dto, @RequestParam(required = false) String coupon) {
        OrderDTO created = orderService.createOrder(dto, coupon);
        if (created != null) {
            return ResponseEntity.ok(created);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/user/{userId}")
    public List<OrderDTO> getUserOrders(@PathVariable Long userId) {
        return orderService.getUserOrders(userId);
    }
}