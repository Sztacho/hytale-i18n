package com.stachu.i18n;

import java.util.Locale;
import java.util.Optional;

public interface MessageSource {
    Optional<String> resolve(String key, Locale locale);

    default String getOrDefault(String key, Locale locale, String fallback) {
        return resolve(key, locale).orElse(fallback);
    }
}