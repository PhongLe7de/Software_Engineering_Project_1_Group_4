package com.otp.whiteboard.dto.user;

import com.otp.whiteboard.enums.Status;
import com.otp.whiteboard.model.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DisplayName("Test UserDto creation and field access")
    @Test
    public void testCreationAndFieldAccess() {

        final UserDto userDto = new UserDto(
                new User(
                        1L,
                        "test@example.com",
                        "TestUser",
                        "123456",
                        Status.ACTIVE
                )
        );

        Set<ConstraintViolation<UserDto>> constraintViolations = validator.validate(userDto);
        assertTrue(constraintViolations.isEmpty(), "There should be no validation violations for valid UserDto");
        assertEquals(1L, userDto.id());
        assertEquals("test@example.com", userDto.email());
        assertEquals("TestUser", userDto.displayName());
        assertEquals(Status.ACTIVE, userDto.status());
    }


    @DisplayName("Test UserDto with null fields")
    @Test
    public void testNullFields() {
        final UserDto userDto = new UserDto(
                new User(
                        1L,
                        null,
                        "TestUser",
                        "en-US",
                        Status.ACTIVE
                )
        );

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }
}