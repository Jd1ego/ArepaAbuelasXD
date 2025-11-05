package com.example.ArepaAbuelas.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users") // ✅ Evita conflicto con palabra reservada 'user'
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String photoUrl; // Ruta de la foto subida

    @Column(nullable = false)
    private boolean approved = false;

    @Column(nullable = false)
    private String role = "USER"; // Valores posibles: USER o ADMIN
}
