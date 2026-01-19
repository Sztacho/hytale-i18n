package com.stachu.i18n;

import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;

public final class ClasspathResourceAccess implements ResourceAccess {

    private final Class<?> anchor;

    public ClasspathResourceAccess(Class<?> anchor) {
        this.anchor = Objects.requireNonNull(anchor);
    }

    @Override
    public Optional<InputStream> open(String path) {
        if (path == null || path.isBlank()) return Optional.empty();
        String p = path.startsWith("/") ? path : "/" + path;
        return Optional.ofNullable(anchor.getResourceAsStream(p));
    }
}