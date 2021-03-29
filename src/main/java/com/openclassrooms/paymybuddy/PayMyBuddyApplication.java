package com.openclassrooms.paymybuddy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class PayMyBuddyApplication {
	/**
	 * Main Application.
	 * @param args .
	 */
	public static void main(final String[] args) {
		SpringApplication.run(PayMyBuddyApplication.class, args);
	}
}
