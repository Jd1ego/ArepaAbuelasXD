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

	@Bean
	public CommandLineRunner initData(
			ProductRepository productRepo,
			CouponRepository couponRepo,
			UserRepository userRepo,
			PasswordEncoder encoder) {

		return args -> {

			// ✅ Crear admin por defecto si no existe
			if (userRepo.findByEmail("admin@arepabuelas.com").isEmpty()) {
				User admin = new User();
				admin.setName("Administrador");
				admin.setEmail("admin@arepabuelas.com");
				admin.setPassword(encoder.encode("arepa123"));
				admin.setRole("ADMIN");
				admin.setApproved(true);          // admin ya está aprobado
				admin.setPhotoUrl(null);          // ⛔ No requiere foto

				userRepo.save(admin);

				System.out.println("\n✅ ADMIN CREADO AUTOMÁTICAMENTE");
				System.out.println("   Email: admin@arepabuelas.com");
				System.out.println("   Password: arepa123\n");
			}

			// ✅ Insertar productos por defecto
			if (productRepo.count() == 0) {
				String[] nombres = {
						"Arepa de Queso",
						"Arepa de Chocolo",
						"Arepa con Hogao",
						"Arepa Reina Pepiada",
						"Arepa Paisa"
				};

				for (int i = 0; i < 5; i++) {
					Product p = new Product();
					p.setName(nombres[i]);
					p.setDescription("La arepa más rica de Ventaquemada, hecha por las abuelas");
					p.setPrice(8000 + i * 2000);
					p.setImageUrl(null);  // ⛔ sin imagen obligatoria
					productRepo.save(p);
				}

				System.out.println("✅ 5 productos agregados");
			}

			// ✅ Crear cupón por defecto
			if (couponRepo.findByCode("AREPABUELAS10").isEmpty()) {
				Coupon coupon = new Coupon();
				coupon.setCode("AREPABUELAS10");
				coupon.setDiscount(0.15); // 15%
				coupon.setForNewUsersOnly(true);
				couponRepo.save(coupon);

				System.out.println("✅ Cupón AREPABUELAS10 creado");
			}
		};
	}
}
