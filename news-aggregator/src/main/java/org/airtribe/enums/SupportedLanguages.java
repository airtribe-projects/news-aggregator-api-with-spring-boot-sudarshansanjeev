package org.airtribe.enums;

import java.util.Locale;

public enum SupportedLanguages {


    ARABIC("ar"),
    GERMAN("de"),
    ENGLISH("en"),
    SPANISH("es"),
    FRENCH("fr"),
    HEBREW("he"),
    ITALIAN("it"),
    DUTCH("nl"),
    NORWEGIAN("no"),
    PORTUGUESE("pt"),
    RUSSIAN("ru"),
    SWEDISH("sv"),
    URDU("ud"),
    CHINESE("zh");

    private final String code;

    SupportedLanguages(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getLanguage() {
        return new Locale(code).getDisplayLanguage(Locale.ENGLISH);
    }
}
