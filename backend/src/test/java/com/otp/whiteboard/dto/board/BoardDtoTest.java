package com.otp.whiteboard.dto.board;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BoardDtoTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
         ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
         validator = factory.getValidator();
    }

    @DisplayName("Test BoardDto Creation")
    @Test
    void testBoardCreation() {
        final BoardDto boardDto = new BoardDto(
                1L,
                "Team Meeting",
                2L,
                List.of(2L, 3L, 4L),
                3,
                10,
                null,
                "Let's have a productive meeting!"
        );
        assertNotNull(boardDto);
        assertEquals("Team Meeting", boardDto.boardName());
        assertEquals(3, boardDto.amountOfUsers());
    }

    @DisplayName("Test BoardDto Validation")
    @Test
    void testBoardDtoValidation() {
        final BoardDto boardDto = new BoardDto(
                1L,
                "Team Meeting",
                2L,
                List.of(2L, 3L, 4L),
                3,
                10,
                null,
                "Let's have a productive meeting!"
        );
        Set<ConstraintViolation<BoardDto>> violations = validator.validate(boardDto);
        assertTrue(violations.isEmpty(), "Valid BoardDto should have no violations");
    }

    @DisplayName("Test BoardDto with Empty User IDs")
    @Test
    void testBoardDtoWithOwnerIds() {
        final BoardDto boardDto = new BoardDto(
                1L,
                "Team Meeting",
                null,
                List.of(),
                0,
                10,
                null,
                "Let's have a productive meeting!"
        );
        Set<ConstraintViolation<BoardDto>> violations = validator.validate(boardDto);
        assertFalse(violations.isEmpty(), "BoardDto with empty user IDs should have violations");
    }

}