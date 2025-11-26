package com.otp.whiteboard.dto.board;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BoardCreatingRequestTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        // Initialize the validator here
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DisplayName("Test BoardCreatingRequest Validation")
    @Test
    void testBoardCreatingRequestValidation() {
        // Create a valid BoardCreatingRequest instance
        BoardCreatingRequest validRequest = new BoardCreatingRequest(
                "Project Kickoff",
                1L,
                "Welcome to the project!"
        );
        Set<ConstraintViolation<BoardCreatingRequest>> violations = validator.validate(validRequest);
        assertTrue(violations.isEmpty(), "Valid request should have no violations");
    }

    @DisplayName("Test BoardCreatingRequest with Null Board Name")
    @Test
    void testBoardCreatingRequestWithNullBoardName() {
        BoardCreatingRequest invalidRequest = new BoardCreatingRequest(
                null,
                1L,
                "Welcome to the project!"
        );
        Set<ConstraintViolation<BoardCreatingRequest>> violations = validator.validate(invalidRequest);
        assertFalse(violations.isEmpty(), "Request with null board name should have violations");
    }

    @DisplayName("Test BoardCreatingRequest with Null Owner ID")
    @Test
    void testBoardCreatingRequestWithNullOwnerId() {
        BoardCreatingRequest invalidRequest = new BoardCreatingRequest(
                "Project Kickoff",
                null,
                "Welcome to the project!"
        );
        Set<ConstraintViolation<BoardCreatingRequest>> violations = validator.validate(invalidRequest);
        assertFalse(violations.isEmpty(), "Request with null owner ID should have violations");
    }
}