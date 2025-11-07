package com.example.ArepaAbuelas.service;

import com.example.ArepaAbuelas.entity.Card;
import com.example.ArepaAbuelas.repository.CardRepository;
import com.example.ArepaAbuelas.util.AesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    @Value("${app.aes.key}")
    private String aesKey;

    public Card saveCard(Long userId, String number, String holder, String expiry, String cvv) {
        Card c = new Card();
        c.setUserId(userId);
        c.setCardHolder(holder);
        c.setExpiry(expiry);
        // store only encrypted full number and cvv; keep last4 in plain for display
        c.setCardNumberEncrypted(AesUtil.encrypt(number, aesKey));
        c.setCvvEncrypted(AesUtil.encrypt(cvv, aesKey));
        if (number != null && number.length() >= 4) {
            c.setLast4(number.substring(number.length() - 4));
        } else {
            c.setLast4("");
        }
        return cardRepository.save(c);
    }

    public List<Card> getCardsForUser(Long userId) {
        return cardRepository.findByUserId(userId);
    }

    public Card getCardIfOwner(Long cardId, Long userId) {
        return cardRepository.findById(cardId)
                .filter(c -> c.getUserId().equals(userId))
                .orElse(null);
    }

    // helper to decrypt when you really need full number (avoid using often)
    public String decryptNumber(Card c) {
        if (c == null || c.getCardNumberEncrypted() == null) return null;
        return AesUtil.decrypt(c.getCardNumberEncrypted(), aesKey);
    }

    public String decryptCvv(Card c) {
        if (c == null || c.getCvvEncrypted() == null) return null;
        return AesUtil.decrypt(c.getCvvEncrypted(), aesKey);
    }
}
