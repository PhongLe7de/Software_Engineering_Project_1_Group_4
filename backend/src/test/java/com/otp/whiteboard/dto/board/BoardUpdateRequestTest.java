package com.otp.whiteboard.dto.board;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardUpdateRequestTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DisplayName("Test BoardUpdateRequest Validation")
    @Test
    public void testBoardUpdateRequestValidation() {
        BoardUpdateRequest validRequest = new BoardUpdateRequest(
                "Updated Board Name",
                15,
                "Updated custom message"
        );
        var violations = validator.validate(validRequest);
        assertTrue(violations.isEmpty(), "Valid request should have no violations");
    }

    @DisplayName("Test BoardUpdateRequest with Null Fields")
    @Test
    public void testBoardUpdateRequestWithNullFields() {
        BoardUpdateRequest requestWithNulls = new BoardUpdateRequest(
                null,
                null,
                null
        );
        var violations = validator.validate(requestWithNulls);
        assertTrue(violations.isEmpty(), "Request with null fields should have no violations");
    }
}