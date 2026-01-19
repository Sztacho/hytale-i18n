package com.stachu.i18n;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class EnumSupport<E extends Enum<E> & LocalizableEnum> {

    private static final ConcurrentMap<Class<?>, Map<String, ?>> KEY_INDEX_CACHE = new ConcurrentHashMap<>();

    private final Class<E> enumClass;
    private final EnumLocalizer localizer;
    private final Map<String, E> byKey;

    private EnumSupport(Class<E> enumClass, EnumLocalizer localizer) {
        this.enumClass = Objects.requireNonNull(enumClass);
        this.localizer = Objects.requireNonNull(localizer);
        this.byKey = keyIndex(enumClass);
    }

    public static <E extends Enum<E> & LocalizableEnum> EnumSupport<E> of(Class<E> enumClass, EnumLocalizer localizer) {
        return new EnumSupport<>(enumClass, localizer);
    }

    public Optional<E> byKey(String key) {
        if (key == null || key.isBlank()) return Optional.empty();
        return Optional.ofNullable(byKey.get(key));
    }

    public String label(String key, Locale locale) {
        return localizer.label(enumClass, key, locale);
    }

    public String label(E value, Locale locale) {
        if (value == null) return "";
        return label(value.getKey(), locale);
    }

    public List<E> values() {
        return List.of(enumClass.getEnumConstants());
    }

    @SuppressWarnings("unchecked")
    private static <E extends Enum<E> & LocalizableEnum> Map<String, E> keyIndex(Class<E> enumClass) {
        return (Map<String, E>) KEY_INDEX_CACHE.computeIfAbsent(enumClass, cls -> {
            E[] constants = enumClass.getEnumConstants();
            if (constants == null) throw new IllegalArgumentException("Not an enum: " + enumClass);

            Map<String, E> map = new LinkedHashMap<>();
            for (E e : constants) {
                String k = e.getKey();
                if (k == null || k.isBlank()) {
                    throw new IllegalStateException("Empty key in " + enumClass.getName() + ": " + e.name());
                }
                if (map.putIfAbsent(k, e) != null) {
                    throw new IllegalStateException("Duplicate key in " + enumClass.getName() + ": " + k);
                }
            }
            return Collections.unmodifiableMap(map);
        });
    }
}