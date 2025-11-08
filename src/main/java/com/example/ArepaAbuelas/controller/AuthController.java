package com.example.ArepaAbuelas.controller;

import com.example.ArepaAbuelas.dto.UserDTO;
import com.example.ArepaAbuelas.entity.User;
import com.example.ArepaAbuelas.repository.UserRepository;
import com.example.ArepaAbuelas.security.JwtUtil;
import com.example.ArepaAbuelas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;


    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(
            @RequestPart("user") UserDTO userRequest,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {

        try {
            userService.register(userRequest, photo);

            return ResponseEntity.ok(
                    "✅ Usuario registrado correctamente. ✅ Pendiente de aprobación del administrador."
            );

        } catch (IOException e) {
            return ResponseEntity.status(500).body("❌ Error al guardar la imagen: " + e.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("❌ " + ex.getMessage());
        }
    }


    // ✅ LOGIN (solamente genera token si el usuario está aprobado)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElse(null);

        if (user == null)
            return ResponseEntity.status(401).body("❌ Usuario no encontrado");

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
            return ResponseEntity.status(401).body("❌ Contraseña incorrecta");

        if (!user.isApproved())
            return ResponseEntity.status(403).body("⚠️ Usuario pendiente de aprobación por el administrador");

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole(), true);

        return ResponseEntity.ok(new AuthResponse(token, user.getRole()));
    }


    // ✅ Clase para devolver token + rol
    public static class AuthResponse {
        private String token;
        private String role;

        public AuthResponse(String token, String role) {
            this.token = token;
            this.role = role;
        }

        public String getToken() { return token; }
        public String getRole() { return role; }
    }
}
