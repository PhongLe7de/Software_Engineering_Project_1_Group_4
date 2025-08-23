package com.otp.whiteboard;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WhiteboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(WhiteboardApplication.class, args);
	}

	@Bean
	public CommandLineRunner testDb(JdbcTemplate jdbcTemplate) {
		return args -> {
			Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
			System.out.println("DB Connection Test Result: " + result);
		};
	}
}
