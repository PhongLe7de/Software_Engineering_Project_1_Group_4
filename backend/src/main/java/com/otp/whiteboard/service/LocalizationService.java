package com.otp.whiteboard.service;

import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@Service
public class LocalizationService {
    public String getMessage(String key, String localeCode) {
        try {
            final Locale locale = getLocaleFromCode(localeCode);
            final ResourceBundle resources = ResourceBundle.getBundle("MessageBundle", locale);
            return resources.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

private Locale getLocaleFromCode(String localeCode) {
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


