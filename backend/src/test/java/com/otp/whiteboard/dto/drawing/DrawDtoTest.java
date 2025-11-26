package com.otp.whiteboard.dto.drawing;

import com.otp.whiteboard.enums.DrawEventType;
import com.otp.whiteboard.enums.DrawingTool;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DrawDtoTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
      ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DisplayName("Test DrawDto creation and field access")
    @Test
    public void testCreationAndFieldAccess() {
        final DrawDto drawDto = new DrawDto(
                "line",
                1L,
                "Alice",
                System.currentTimeMillis(),
                DrawEventType.DRAW,
                DrawingTool.PEN,
                0.0,
                0.0,
                5L,
                "#FF0000",
                "stroke123"
        );
        Set<ConstraintViolation<DrawDto>> violations = validator.validate(drawDto);

        assertTrue(violations.isEmpty(), "There should be no validation violations for valid DrawDto");
        assertEquals("line", drawDto.id());
        assertEquals(1L, drawDto.boardId());
        assertEquals("Alice", drawDto.displayName());
        assertEquals(DrawEventType.DRAW, drawDto.type());
        assertEquals(DrawingTool.PEN, drawDto.tool());
        assertEquals(0.0, drawDto.x());
        assertEquals(0.0, drawDto.y());
        assertEquals(5L, drawDto.brushSize());
        assertEquals("#FF0000", drawDto.brushColor());
        assertEquals("stroke123", drawDto.strokeId());
    }

    @DisplayName("Test DrawDto with null optional fields")
    @Test
    public void testNullOptionalFields() {
        final DrawDto drawDto = new DrawDto(
                "line",
                1L,
                "Alice",
                System.currentTimeMillis(),
                DrawEventType.DRAW,
                DrawingTool.PEN,
                0.0,
                0.0,
                null,
                null,
                null
        );
        Set<ConstraintViolation<DrawDto>> violations = validator.validate(drawDto);
        assertTrue(violations.isEmpty(), "There should be no validation violations for DrawDto with null optional fields");
        assertNull(drawDto.brushSize());
        assertNull(drawDto.brushColor());
        assertNull(drawDto.strokeId());
    }

}