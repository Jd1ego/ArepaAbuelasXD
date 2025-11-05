package com.example.ArepaAbuelas.service;


import com.arepabuelas.dto.ProductDTO;
import com.arepabuelas.entity.Product;
import com.arepabuelas.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public ProductDTO createProduct(ProductDTO dto, MultipartFile image) throws IOException {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());

        if (image != null) {
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            image.transferTo(new File("uploads/" + fileName));
            product.setImageUrl("/uploads/" + fileName);
        }

        product = productRepository.save(product);
        dto.setId(product.getId());
        dto.setImageUrl(product.getImageUrl());
        return dto;
    }

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream().map(p -> {
            ProductDTO dto = new ProductDTO();
            dto.setId(p.getId());
            dto.setName(p.getName());
            dto.setDescription(p.getDescription());
            dto.setPrice(p.getPrice());
            dto.setImageUrl(p.getImageUrl());
            return dto;
        }).collect(Collectors.toList());
    }

    public Optional<ProductDTO> getProductById(Long id) {
        return productRepository.findById(id).map(p -> {
            ProductDTO dto = new ProductDTO();
            dto.setId(p.getId());
            dto.setName(p.getName());
            dto.setDescription(p.getDescription());
            dto.setPrice(p.getPrice());
            dto.setImageUrl(p.getImageUrl());
            return dto;
        });
    }
}