package com.example.ArepaAbuelas.dto;

import lombok.Data;

@Data
public class CommentDTO {
    private Long id;
    private String text;
    private Long userId;
    private Long productId;
}