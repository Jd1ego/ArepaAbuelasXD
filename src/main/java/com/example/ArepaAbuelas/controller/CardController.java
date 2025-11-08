package com.example.ArepaAbuelas.controller;

import com.example.ArepaAbuelas.dto.CardDTO;

import com.example.ArepaAbuelas.entity.Card;
import com.example.ArepaAbuelas.entity.User;
import com.example.ArepaAbuelas.service.CardService;
import com.example.ArepaAbuelas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cards")
@CrossOrigin(origins = "*")
public class CardController {

    @Autowired private CardService cardService;
    @Autowired private UserService userService;

    @PostMapping
    public ResponseEntity<?> saveCard(@RequestBody CardDTO req) {

        if (req.getCardNumber() == null || req.getCardNumber().length() < 12) {
            return ResponseEntity.badRequest().body("Invalid card number");
        }
        if (req.getCvv() == null || req.getCvv().length() < 3) {
            return ResponseEntity.badRequest().body("Invalid CVV");
        }

        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User u = userService.findByEmail(principal);
        if (u == null) return ResponseEntity.status(401).body("Not authenticated");

        Card saved = cardService.saveCard(u.getId(), req.getCardNumber(), req.getCardHolder(), req.getExpiry(), req.getCvv());

        return ResponseEntity.ok(new CardSummary(saved.getId(), saved.getLast4(), saved.getCardHolder(), saved.getExpiry()));
    }

    @GetMapping
    public ResponseEntity<?> myCards() {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User u = userService.findByEmail(principal);
        if (u == null) return ResponseEntity.status(401).body("Not authenticated");

        List<Card> list = cardService.getCardsForUser(u.getId());
        List<CardSummary> out = list.stream()
                .map(c -> new CardSummary(c.getId(), c.getLast4(), c.getCardHolder(), c.getExpiry()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }


    public static record CardSummary(Long id, String last4, String cardHolder, String expiry) {}
}
