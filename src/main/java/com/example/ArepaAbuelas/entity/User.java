package com.example.ArepaAbuelas.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    private String password;

    private String photoPath;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.PENDING;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderEntity> orders;

    public enum Role { USER, ADMIN }
    public enum UserStatus { PENDING, ACTIVE, BLOCKED }
}
