package com.example.ArepaAbuelas.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String password;
    private String photoUrl;
    private boolean approved;
    private String role;
}
