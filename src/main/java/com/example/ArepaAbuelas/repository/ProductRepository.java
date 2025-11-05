package com.example.ArepaAbuelas.repository;
import com.arepabuelas.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}