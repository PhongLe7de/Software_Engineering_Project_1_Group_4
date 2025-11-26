package com.otp.whiteboard.dto.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserByDisplayNameRequestTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        // Validator setup code would go here
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DisplayName("Test UserByDisplayNameRequest creation and field access")
    @Test
    public void testUserByDisplayNameRequestCreationAndFieldAccess() {
        final UserByDisplayNameRequest request = new UserByDisplayNameRequest("TestUser");
        Set<ConstraintViolation<UserByDisplayNameRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "There should be no validation violations for valid UserByDisplayNameRequest");
        assertEquals("TestUser", request.displayName());
    }

    @DisplayName("Test UserByDisplayNameRequest with null displayName")
    @Test
    public void testUserByDisplayNameRequestWithNullDisplayName() {
        final UserByDisplayNameRequest request = new UserByDisplayNameRequest(null );
        Set<ConstraintViolation<UserByDisplayNameRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "There should be no validation violations for UserByDisplayNameRequest with null displayName");

    }

}