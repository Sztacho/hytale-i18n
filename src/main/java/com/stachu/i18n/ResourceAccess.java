package com.stachu.i18n;

import java.io.InputStream;
import java.util.Optional;

public interface ResourceAccess {
    Optional<InputStream> open(String path);
}