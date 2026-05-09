package com.ecommerce.cartrecovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CartrecoveryApplication {

	public static void main(String[] args) {
		SpringApplication.run(CartrecoveryApplication.class, args);
	}

}

/***
 * checkout cart event
 * add logs
 * schedule for next stage
 */