package com.example.ArepaAbuelas.service;

import com.example.ArepaAbuelas.dto.UserDTO;
import com.example.ArepaAbuelas.entity.User;
import com.example.ArepaAbuelas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDTO register(UserDTO dto, MultipartFile photo) throws IOException {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        // Asigna rol por defecto
        user.setRole("ROLE_USER");

        // Auto aprobar el usuario (puedes cambiar a false si solo el admin debe aprobar)
        user.setApproved(true);

        // Guardar foto si se adjunta
        if (photo != null && !photo.isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + photo.getOriginalFilename();
            File uploadDir = new File("uploads");
            if (!uploadDir.exists()) uploadDir.mkdirs();
            photo.transferTo(new File(uploadDir, fileName));
            user.setPhotoUrl("/uploads/" + fileName);
        }

        user = userRepository.save(user);

        dto.setId(user.getId());
        dto.setPassword(null);
        dto.setRole(user.getRole());
        dto.setPhotoUrl(user.getPhotoUrl());
        return dto;
    }

    public UserDTO login(String email, String password) {
        Optional<User> optUser = userRepository.findByEmail(email);
        if (optUser.isPresent()) {
            User user = optUser.get();
            if (passwordEncoder.matches(password, user.getPassword()) && user.isApproved()) {
                UserDTO dto = new UserDTO();
                dto.setId(user.getId());
                dto.setName(user.getName());
                dto.setEmail(user.getEmail());
                dto.setPhotoUrl(user.getPhotoUrl());
                dto.setRole(user.getRole());
                return dto;
            }
        }
        return null;
    }

    public List<UserDTO> getPendingUsers() {
        return userRepository.findAll().stream()
                .filter(u -> !u.isApproved())
                .map(u -> {
                    UserDTO dto = new UserDTO();
                    dto.setId(u.getId());
                    dto.setName(u.getName());
                    dto.setEmail(u.getEmail());
                    dto.setPhotoUrl(u.getPhotoUrl());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public void approveUser(Long id) {
        Optional<User> optUser = userRepository.findById(id);
        optUser.ifPresent(user -> {
            user.setApproved(true);
            userRepository.save(user);
        });
    }

    // ✅ MÉTODO AÑADIDO: usado en CardController
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}
