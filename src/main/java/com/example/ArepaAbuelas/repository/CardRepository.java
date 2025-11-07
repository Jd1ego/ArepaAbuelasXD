package com.example.ArepaAbuelas.repository;

import com.example.ArepaAbuelas.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByUserId(Long userId);
}
