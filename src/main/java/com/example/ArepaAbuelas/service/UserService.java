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

        // ✨ CORREGIDO -> ahora guardamos el rol con el prefijo obligatorio
        user.setRole("ROLE_USER");

        // Auto-approve on register to allow immediate JWT access (change if you want moderation)
        user.setApproved(true);

        if (photo != null) {
            String fileName = UUID.randomUUID() + "_" + photo.getOriginalFilename();
            photo.transferTo(new File("uploads/" + fileName));
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

                // ✨ CORREGIDO -> devolvemos el rol con formato ROLE_*
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
                }).collect(Collectors.toList());
    }

    public void approveUser(Long id) {
        Optional<User> optUser = userRepository.findById(id);
        optUser.ifPresent(user -> {
            user.setApproved(true);
            userRepository.save(user);
        });
    }
}
