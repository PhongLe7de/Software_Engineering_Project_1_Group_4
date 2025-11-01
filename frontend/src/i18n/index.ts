import i18n from 'i18next';
import {initReactI18next} from
        'react-i18next';
import LanguageDetector from
        'i18next-browser-languagedetector';

import en from './locales/en.json';
import ja from "./locales/ja.json";
import ru from './locales/ru.json';
import zh from './locales/zh.json';

i18n.use(LanguageDetector) // Detects browser language and automatically sets app localization
    .use(initReactI18next)
    .init({
            resources: {
                en: {translation: en},
                ja: {translation: ja},
                ru: {translation: ru},
                zh: {translation: zh},
            },
            fallbackLng: 'en',
            debug: false,
            interpolation: {
                escapeValue: false,
            },
            detection: {
                order: ['localStorage', 'navigator', 'htmlTag'],
                caches: ['localStorage'],
            },
        });

export default i18n;
