package com.example.ArepaAbuelas.service;

import com.example.ArepaAbuelas.entity.User;
import com.example.ArepaAbuelas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        // Bloquea login si no está aprobado
        if (!user.isApproved()) {
            throw new DisabledException("Cuenta pendiente de aprobación por el administrador");
        }

        // Construye autoridad con prefijo ROLE_
        String role = user.getRole().startsWith("ROLE_") ? user.getRole() : "ROLE_" + user.getRole();
        var authority = new SimpleGrantedAuthority(role);

        // Usa el builder de Spring Security (org.springframework.security.core.userdetails.User)
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(authority)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false) // Ya validamos arriba con DisabledException
                .build();
    }
}