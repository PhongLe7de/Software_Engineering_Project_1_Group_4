package com.otp.whiteboard;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class WhiteboardApplicationTests {

	@Test
	void contextLoads() {
		assertTrue(true);
	}

}
