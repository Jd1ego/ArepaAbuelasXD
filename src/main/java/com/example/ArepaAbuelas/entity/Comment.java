// src/main/java/com/arepabuelas/entity/Comment.java
package com.example.ArepaAbuelas.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    @ManyToOne
    private User user;

    @ManyToOne
    private Product product;
}