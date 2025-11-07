package com.example.ArepaAbuelas.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    // Guardamos la tarjeta cifrada
    @Column(length = 1000)
    private String cardNumberEncrypted;

    private String last4; // para mostrar en UI

    private String cardHolder;

    private String expiry; // MM/YY

    @Column(length = 1000)
    private String cvvEncrypted;
}
