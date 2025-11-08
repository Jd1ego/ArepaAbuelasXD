// src/main/java/com/example/ArepaAbuelas/ArepabuelasApplication.java
package com.example.ArepaAbuelas;

import com.example.ArepaAbuelas.entity.Coupon;
import com.example.ArepaAbuelas.entity.Product;
import com.example.ArepaAbuelas.entity.User;
import com.example.ArepaAbuelas.repository.CouponRepository;
import com.example.ArepaAbuelas.repository.ProductRepository;
import com.example.ArepaAbuelas.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ArepabuelasApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArepabuelasApplication.class, args);
	}


}
