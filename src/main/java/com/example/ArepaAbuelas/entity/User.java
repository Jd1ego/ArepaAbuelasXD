package com.example.ArepaAbuelas.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    private String password; // Hashed

    private String photoUrl; // Path to uploaded photo

    private boolean approved = false; // Admin approves

    private String role = "USER"; // USER or ADMIN
}