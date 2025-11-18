package com.otp.whiteboard.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LocalizationServiceTest {
    @Mock
    private LocalizationService localizationService;

    @BeforeEach
    void setupTestTarget() {
        localizationService = new LocalizationService();
    }
    @Test
    void getMessageWithDifferentLocales() {
        // given
        final String missingKey = "missing_key";
        // when & then
        final String result = localizationService.getMessage(missingKey, "en");

        assertNotNull(localizationService.getMessage("welcome", "en"));
        assertNotNull(localizationService.getMessage("welcome", "ru"));
        assertNotNull(localizationService.getMessage("welcome", "zh"));
        assertNotNull(localizationService.getMessage("welcome", "ja"));
        assertNotNull(localizationService.getMessage("welcome", "vi"));
        assertNotNull(localizationService.getMessage("welcome", "xx"));

        assertEquals(missingKey, result);
    }
}