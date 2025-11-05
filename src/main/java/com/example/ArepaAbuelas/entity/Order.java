package com.example.ArepaAbuelas.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "orders") // Avoid keyword conflict
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderItem> items;

    private LocalDateTime date;
    private double total;

    // Simulated credit card data (DO NOT USE REAL CARDS)
    private String cardNumber; // Encrypted or hashed in real scenario
    private String expiry;
    private String cvv;
}