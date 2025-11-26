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

class ModifyBoardUserRequestTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DisplayName("Test ModifyBoardUserRequest with valid userId")
    @Test
    void testValidUserId() {
        ModifyBoardUserRequest request = new ModifyBoardUserRequest(1L);
        Set<ConstraintViolation<ModifyBoardUserRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "There should be no validation violations for a valid userId");
    }

    @DisplayName("Test ModifyBoardUserRequest with null userId")
    @Test
    void testNullUserId() {
        ModifyBoardUserRequest request = new ModifyBoardUserRequest(null);
        Set<ConstraintViolation<ModifyBoardUserRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "There should be validation violations for a null userId");
    }

}