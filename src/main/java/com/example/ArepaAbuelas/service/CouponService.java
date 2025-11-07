package com.example.ArepaAbuelas.service;

import com.example.ArepaAbuelas.entity.Coupon;
import com.example.ArepaAbuelas.entity.User;
import com.example.ArepaAbuelas.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    // For simplicity, no used coupons tracking
    public Optional<Double> applyCoupon(User user, String code) {
        Optional<Coupon> optCoupon = couponRepository.findByCode(code);
        if (optCoupon.isPresent()) {
            Coupon coupon = optCoupon.get();
            if (coupon.isForNewUsersOnly() && getUserOrderCount(user.getId()) == 0) { // Assume method exists
                return Optional.of(coupon.getDiscount());
            } else if (!coupon.isForNewUsersOnly()) {
                return Optional.of(coupon.getDiscount());
            }
        }
        return Optional.empty();
    }

    private int getUserOrderCount(Long userId) {
        // Implement or inject OrderRepository to count
        return 0; // Placeholder
    }
}