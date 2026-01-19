package com.stachu.i18n;

import java.util.Locale;
import java.util.Objects;

public final class EnumLocalizer {

    public interface KeyStrategy {
        String toMessageKey(Class<?> enumClass, String enumKey);
    }

    public static final KeyStrategy DEFAULT = (cls, key) -> cls.getSimpleName() + "." + key;

    private final MessageSource messages;
    private final KeyStrategy keyStrategy;

    public EnumLocalizer(MessageSource messages) {
        this(messages, DEFAULT);
    }

    public EnumLocalizer(MessageSource messages, KeyStrategy keyStrategy) {
        this.messages = Objects.requireNonNull(messages);
        this.keyStrategy = Objects.requireNonNull(keyStrategy);
    }

    public String label(Class<?> enumClass, String enumKey, Locale locale) {
        if (enumKey == null || enumKey.isBlank()) return "";
        String msgKey = keyStrategy.toMessageKey(enumClass, enumKey);
        return messages.getOrDefault(msgKey, locale, enumKey);
    }
}