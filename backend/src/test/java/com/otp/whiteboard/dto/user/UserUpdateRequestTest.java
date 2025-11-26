package com.otp.whiteboard.dto.user;

import com.otp.whiteboard.enums.Status;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserUpdateRequestTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        // Validator setup code would go here
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DisplayName("Test UserUpdateRequest creation and field access")
    @Test
    public void testCreationAndFieldAccess() {
        final UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
                Status.ACTIVE,
                "http://example.com/newphoto.jpg",
                "NewDisplayName",
                "en-US",
                "newpassword123"
                );
        Set<ConstraintViolation<UserUpdateRequest>> violations = validator.validate(userUpdateRequest);
        assertTrue(violations.isEmpty(), "There should be no validation violations for valid UserUpdateRequest");

        assertEquals("NewDisplayName", userUpdateRequest.displayName());
        assertEquals("http://example.com/newphoto.jpg", userUpdateRequest.photoUrl());
    }

    @DisplayName("Test UserUpdateRequest with null optional fields")
    @Test
    public void testNullOptionalFields() {
        final UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
                null,
                null,
                null,
                null,
                null
        );
        Set<ConstraintViolation<UserUpdateRequest>> violations = validator.validate(userUpdateRequest);
        assertTrue(violations.isEmpty(), "There should be no validation violations for UserUpdateRequest with null optional fields");

    }
}