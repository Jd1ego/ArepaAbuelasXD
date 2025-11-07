
package com.example.ArepaAbuelas.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private double discount;
    private boolean forNewUsersOnly = true;
}