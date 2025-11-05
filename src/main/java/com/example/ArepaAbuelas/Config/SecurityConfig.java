// src/main/java/com/arepabuelas/config/SecurityConfig.java
package com.example.ArepaAbuelas.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. CORS (activamos el bean de abajo)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. CSRF off → API REST sin cookies
                .csrf(csrf -> csrf.disable())

                // 3. Sin sesiones → cada request lleva credenciales
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. Autorización por URL
                .authorizeHttpRequests(auth -> auth
                        // Registro y login → público
                        .requestMatchers("/api/auth/**").permitAll()

                        // Solo ADMIN
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Productos: leer público, comentar solo usuarios autenticados
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/products/**/comments").authenticated()

                        // Pedidos solo usuarios autenticados
                        .requestMatchers("/api/orders/**").authenticated()

                        // Todo lo demás → necesita login
                        .anyRequest().authenticated()
                )

                // 5. Autenticación Basic (para pruebas). Cambia a JWT después.
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // fuerza 12 → más seguro
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:5173", "http://127.0.0.1:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // cookies, Authorization header

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}