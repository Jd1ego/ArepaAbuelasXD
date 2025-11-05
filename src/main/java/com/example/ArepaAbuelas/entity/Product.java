// src/main/java/com/arepabuelas/entity/Product.java
package com.example.ArepaAbuelas.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private double price;
    private String imageUrl; // Path to uploaded image

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Comment> comments;
}