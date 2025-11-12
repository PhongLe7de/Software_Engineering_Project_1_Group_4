package com.otp.whiteboard.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LocalizationServiceTest {

    @Test
    void getMessage() {
        LocalizationService localizationService = new LocalizationService();

        String messageEn = localizationService.getMessage("welcome", "en");
        assertEquals("welcome to our whiteboard!", messageEn);

        String missingKeyMessage = localizationService.getMessage("nonexistent_key", "en");
        assertEquals("nonexistent_key", missingKeyMessage);
    }
}