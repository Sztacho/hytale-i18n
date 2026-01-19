package com.stachu.i18n;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class LangFileMessageSource implements MessageSource {

    private final List<String> pathPatterns;
    private final Locale defaultLocale;
    private final ResourceAccess resources;

    private final ConcurrentMap<String, Map<String, String>> cache = new ConcurrentHashMap<>();

    public LangFileMessageSource(List<String> pathPatterns, Locale defaultLocale, ResourceAccess resources) {
        if (pathPatterns == null || pathPatterns.isEmpty()) throw new IllegalArgumentException("pathPatterns required");
        for (String p : pathPatterns) {
            if (p == null || !p.contains("%s")) throw new IllegalArgumentException("Pattern must contain %s: " + p);
        }
        this.pathPatterns = List.copyOf(pathPatterns);
        this.defaultLocale = Objects.requireNonNull(defaultLocale);
        this.resources = Objects.requireNonNull(resources);
    }

    public void reload() {
        cache.clear();
    }

    @Override
    public Optional<String> resolve(String key, Locale locale) {
        if (key == null || key.isBlank()) return Optional.empty();

        for (String resourcePath : candidateResourcePaths(locale)) {
            Map<String, String> dict = cache.computeIfAbsent(resourcePath, this::loadDictOrEmpty);
            String val = dict.get(key);
            if (val != null) return Optional.of(val);
        }
        return Optional.empty();
    }

    private Map<String, String> loadDictOrEmpty(String path) {
        try (InputStream is = resources.open(path).orElse(null)) {
            if (is == null) return Collections.emptyMap();

            Properties props = new Properties();
            props.load(new InputStreamReader(is, StandardCharsets.UTF_8));

            Map<String, String> map = new HashMap<>();
            for (String k : props.stringPropertyNames()) map.put(k, props.getProperty(k));
            return Collections.unmodifiableMap(map);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    private List<String> candidateResourcePaths(Locale locale) {
        List<String> tags = localeFallbackTags(locale);

        List<String> paths = new ArrayList<>(pathPatterns.size() * tags.size());
        for (String pattern : pathPatterns) {
            for (String tag : tags) {
                paths.add(String.format(Locale.ROOT, pattern, tag));
            }
        }
        return paths;
    }

    private List<String> localeFallbackTags(Locale locale) {
        Locale loc = (locale == null) ? defaultLocale : locale;

        String full = normalizeTag(loc);
        String lang = loc.getLanguage();
        String def = normalizeTag(defaultLocale);

        LinkedHashSet<String> set = new LinkedHashSet<>();
        if (!full.isBlank()) set.add(full);
        if (!lang.isBlank()) set.add(lang);
        set.add(def);

        return new ArrayList<>(set);
    }

    private static String normalizeTag(Locale locale) {
        String tag = locale.toLanguageTag();
        return "und".equalsIgnoreCase(tag) ? "" : tag;
    }
}