package com.otp.whiteboard.service;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@Service
public class LocalizationService {
    /**
     * Retrieves a localized message based on the provided key and locale code.
     *
     * @param key        The key for the desired message.
     * @param localeCode The locale code (e.g., "en", "ru", "zh").
     * @return The localized message if found; otherwise, returns the key itself.
     */
    @Nonnull
    public String getMessage(@NotNull final String key, @NotNull final String localeCode) {
        try {
            final Locale locale = getLocaleFromCode(localeCode);
            final ResourceBundle resources = ResourceBundle.getBundle("MessageBundle", locale);
            return resources.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    /**
     * Maps a locale code to a Locale object.
     *
     * @param localeCode The locale code (e.g., "en", "ru", "zh").
     * @return The corresponding Locale object.
     */
    @Nonnull
    private Locale getLocaleFromCode(@NotNull final String localeCode) {
        return switch (localeCode) {
            case "ru" -> new Locale("ru", "RU");
            case "zh" -> new Locale("zh", "CN");
            case "ja" -> new Locale("ja", "JP");
            case "vi" -> new Locale("vi", "VN");
            case "en" -> new Locale("en", "US");
            default -> Locale.getDefault();
        };
    }
}


