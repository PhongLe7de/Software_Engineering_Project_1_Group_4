package com.otp.whiteboard.dto.drawing;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CursorDtoTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DisplayName("Test CursorDto creation and field access")
    @Test
    public void testCreationAndFieldAccess() {
        CursorDto cursor = new CursorDto("Alice", "http://example.com/photo.jpg", 100.0, 200.0);
        Set<ConstraintViolation<CursorDto>> violations = validator.validate(cursor);
        assertTrue(violations.isEmpty(), "There should be no validation violations for valid Cursor");
        assertEquals("Alice", cursor.getDisplayName());
        assertEquals("http://example.com/photo.jpg", cursor.photoUrl());
        assertEquals(100.0, cursor.x());
        assertEquals(200.0, cursor.y());
    }

    @DisplayName("Test CursorDto with null fields")
    @Test
    public void testNullFields() {
        CursorDto cursor = new CursorDto(null, null, 0.0, 0.0);

        Set<ConstraintViolation<CursorDto>> violations = validator.validate(cursor);

        assertTrue(violations.isEmpty(), "There should be no validation violations for Cursor with null fields");
        assertNull(cursor.getDisplayName());
        assertNull(cursor.photoUrl());
        assertEquals(0.0, cursor.x());
        assertEquals(0.0, cursor.y());
    }
}