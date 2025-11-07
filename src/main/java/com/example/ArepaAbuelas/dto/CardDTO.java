package com.example.ArepaAbuelas.dto;

import lombok.Data;

@Data
public class CardDTO {
    private String cardNumber;
    private String cardHolder;
    private String expiry; // MM/YY
    private String cvv;
}
