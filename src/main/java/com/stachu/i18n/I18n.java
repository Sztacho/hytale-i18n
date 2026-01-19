package com.stachu.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class I18n {

    private I18n() {}

    public static Builder forMod(Class<?> anchor) {
        return builder().resources(new ClasspathResourceAccess(anchor));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final List<String> patterns = new ArrayList<>();
        private Locale defaultLocale = Locale.forLanguageTag("en-US");
        private ResourceAccess resources;

        public Builder addLangFilePattern(String pattern) {
            patterns.add(requireValidPattern(pattern));
            return this;
        }

        public Builder defaultLocale(Locale locale) {
            this.defaultLocale = Objects.requireNonNull(locale, "defaultLocale");
            return this;
        }

        public Builder resources(ResourceAccess resources) {
            this.resources = Objects.requireNonNull(resources, "resources");
            return this;
        }

        public Builder classpath(Class<?> anchor) {
            return resources(new ClasspathResourceAccess(anchor));
        }

        public MessageSource build() {
            if (patterns.isEmpty()) {
                throw new IllegalStateException("No lang file patterns configured. Use addLangDirectory/addLangFilePattern.");
            }
            if (resources == null) {
                throw new IllegalStateException(
                        "No ResourceAccess configured. Use I18n.forMod(anchor) or builder().resources(...)."
                );
            }

            return new LangFileMessageSource(patterns, defaultLocale, resources);
        }

        private static String requireValidPattern(String pattern) {
            String p = Objects.requireNonNull(pattern, "pattern").trim();
            if (p.isBlank()) throw new IllegalArgumentException("pattern cannot be blank");
            if (!p.contains("%s")) throw new IllegalArgumentException("pattern must contain %s placeholder: " + p);
            if (p.startsWith("/")) p = p.substring(1);
            return p;
        }
    }
}
