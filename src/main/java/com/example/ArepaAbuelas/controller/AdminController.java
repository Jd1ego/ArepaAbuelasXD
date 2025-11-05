package com.example.ArepaAbuelas.controller;

import com.arepabuelas.dto.ProductDTO;
import com.arepabuelas.dto.UserDTO;
import com.arepabuelas.service.ProductService;
import com.arepabuelas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @GetMapping("/pending-users")
    public List<UserDTO> getPendingUsers() {
        return userService.getPendingUsers();
    }

    @PostMapping("/approve-user/{id}")
    public ResponseEntity<Void> approveUser(@PathVariable Long id) {
        userService.approveUser(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/products")
    public ResponseEntity<ProductDTO> createProduct(@RequestPart ProductDTO dto, @RequestPart(required = false) MultipartFile image) throws IOException {
        ProductDTO created = productService.createProduct(dto, image);
        return ResponseEntity.ok(created);
    }
}