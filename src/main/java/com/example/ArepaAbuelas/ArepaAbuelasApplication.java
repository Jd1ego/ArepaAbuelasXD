// src/main/java/com/arepabuelas/ArepabuelasApplication.java
package com.example.ArepaAbuelas;

import com.arepabuelas.entity.Coupon;
import com.arepabuelas.entity.Product;
import com.arepabuelas.entity.User;
import com.arepabuelas.repository.CouponRepository;
import com.arepabuelas.repository.ProductRepository;
import com.arepabuelas.repository.UserRepository;
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

	@Bean
	public CommandLineRunner initData(
			ProductRepository productRepo,
			CouponRepository couponRepo,
			UserRepository userRepo,
			PasswordEncoder encoder) {

		return args -> {
			// === ADMIN (listo para usar) ===
			if (userRepo.findByEmail("admin@arepabuelas.com").isEmpty()) {
				User admin = new User();
				admin.setName("Camarón A-Panado");
				admin.setEmail("admin@arepabuelas.com");
				admin.setPassword(encoder.encode("arepa123"));
				admin.setRole("ADMIN");
				admin.setApproved(true);
				admin.setPhotoUrl("/uploads/admin.jpg");
				userRepo.save(admin);
				System.out.println("ADMIN creado: admin@arepabuelas.com / arepa123");
			}

			// === 5 PRODUCTOS ===
			if (productRepo.count() == 0) {
				String[] nombres = {"Arepa de Queso", "Arepa de Chocolo", "Arepa con Hogao", "Arepa Reina Pepiada", "Arepa Paisa"};
				for (int i = 0; i < 5; i++) {
					Product p = new Product();
					p.setName(nombres[i]);
					p.setDescription("La arepa más rica de Ventaquemada, hecha por las abuelas ❤️");
					p.setPrice(8000 + i * 2000);
					p.setImageUrl("/uploads/arepa" + (i + 1) + ".jpg");
					productRepo.save(p);
				}
				System.out.println("5 arepas cargadas");
			}

			// === CUPÓN PARA NUEVOS USUARIOS ===
			if (couponRepo.findByCode("AREPABUELAS10").isEmpty()) {
				Coupon coupon = new Coupon();
				coupon.setCode("AREPABUELAS10");
				coupon.setDiscount(0.15); // 15% OFF
				coupon.setForNewUsersOnly(true);
				couponRepo.save(coupon);
				System.out.println("Cupón AREPABUELAS10 creado (15% OFF para nuevos usuarios)");
			}
		};
	}
}