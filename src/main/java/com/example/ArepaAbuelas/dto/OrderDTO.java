package com.example.ArepaAbuelas.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private Long userId;
    private List<OrderItemDTO> items;
    private LocalDateTime date;
    private double total;
    private String cardNumber;
    private String expiry;
    private String cvv;
}