package com.otp.whiteboard.dto.auth;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Set;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RegisterRequestTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
         ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
         validator = factory.getValidator();
    }

    @DisplayName("should be valid when all fields are provided")
    @Test
    void shouldBeValidWhenAllFieldsProvided() {
        RegisterRequest request = new RegisterRequest(
                "test@example.com",
                "secret123",
                "http://example.com/photo.jpg",
                "Test User",
                "en"
        );
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assert (violations.isEmpty());
    }

    @DisplayName("should fail when email is blank")
    @Test
    void shouldFailWhenEmailIsBlank() {
        RegisterRequest request = new RegisterRequest(
                "",
                "secret123",
                "http://example.com/photo.jpg",
                "Test User",
                "en"
        );
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assert (!violations.isEmpty());
        assert (violations.size() == 1);
    }

    @DisplayName("should fail when password is null")
    @Test
    void shouldFailWhenPasswordIsNull() {
        RegisterRequest request = new RegisterRequest(
                "test@example.com",
                null,
                "http://example.com/photo.jpg",
                "Test User",
                "en"
        );
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assert (!violations.isEmpty());
    }


}