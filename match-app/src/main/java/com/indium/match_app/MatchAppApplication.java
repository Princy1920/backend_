package com.indium.match_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableCaching
@EnableJpaRepositories(basePackages = "com.indium.match_app.repository") // Add this line
public class MatchAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(MatchAppApplication.class, args);
	}
}
