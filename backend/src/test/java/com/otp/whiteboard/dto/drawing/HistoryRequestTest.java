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

class HistoryRequestTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DisplayName("Test HistoryRequest creation and field access")
    @Test
    public void testCreationAndFieldAccess() {
        final HistoryRequest historyRequest = new HistoryRequest(10L, 20);

        Set<ConstraintViolation<HistoryRequest>> violations = validator.validate(historyRequest);

        assertTrue(violations.isEmpty(), "There should be no validation violations for valid HistoryRequest");
        assertEquals(10, historyRequest.boardId());
        assertEquals(20, historyRequest.limit());
    }

    @DisplayName("Test HistoryRequest with null limit")
    @Test
    public void testNullLimit() {
        final HistoryRequest historyRequest = new HistoryRequest(10L, null);
        Set<ConstraintViolation<HistoryRequest>> violations = validator.validate(historyRequest);
        assertTrue(violations.isEmpty(), "There should be no validation violations for HistoryRequest with null limit");
        assertEquals(10, historyRequest.boardId());
        assertNull(historyRequest.limit());
    }
}