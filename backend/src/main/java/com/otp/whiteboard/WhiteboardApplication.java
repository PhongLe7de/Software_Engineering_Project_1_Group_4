package com.otp.whiteboard;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WhiteboardApplication {
	private final Logger logger = LoggerFactory.getLogger(WhiteboardApplication.class);


	public static void main(String[] args) {
		SpringApplication.run(WhiteboardApplication.class, args);
	}

	@Bean
	public CommandLineRunner testDb(JdbcTemplate jdbcTemplate) {

		return args -> {
			Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
			logger.info("DB Connection Test Result: " + result);
		};
	}
}
