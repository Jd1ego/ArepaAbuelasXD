package com.example.ArepaAbuelas.controller;

import com.example.ArepaAbuelas.dto.UserDTO;
import com.example.ArepaAbuelas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestPart UserDTO dto, @RequestPart(required = false) MultipartFile photo) throws IOException {
        UserDTO registered = userService.register(dto, photo);
        return ResponseEntity.ok(registered);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody UserDTO dto) {
        UserDTO loggedIn = userService.login(dto.getEmail(), dto.getPassword());
        if (loggedIn != null) {
            return ResponseEntity.ok(loggedIn);
        }
        return ResponseEntity.badRequest().build();
    }
}