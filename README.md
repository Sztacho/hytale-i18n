# hytale-i18n

A small utility library that makes localization (i18n) in Hytale mods easier:
- Load `.lang` files per locale using a path pattern
- Translate enums via stable keys (`LocalizableEnum` + `EnumSupport`)
- Automatic fallback to a default locale

---

## Requirements

- Java 17+ (Java 21 recommended)
- Gradle (Kotlin DSL)

---

## Installation (Gradle)

This library is published as a Maven repository on GitHub Pages.

### 1) Add the repository

**Recommended (most reliable):** add it to `settings.gradle.kts` (many templates disallow project-level repositories in `build.gradle.kts`):

```kotlin
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://sztacho.github.io/hytale-i18n/")
        maven("https://maven.hytale-modding.info/releases")
    }
}
```

If your project does not use `dependencyResolutionManagement`, add it to your mod’s `build.gradle.kts` instead:

```kotlin
repositories {
    ...
    //Add this maven rep
    maven("https://sztacho.github.io/hytale-i18n/")
    ...
}
```

### 2) Add the dependency

In your mod’s `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.stachu:hytale-i18n:0.0.1")
}
```

> Use the version that is actually published (e.g. `0.0.1`).

---

## Quick start

### Recommended key convention

For enum labels, keep keys simple and stable:

```
<EnumAlias>.<enumKey>
```

Example:
- `ArticleStatus.draft`
- `ArticleStatus.published`
- `ArticleStatus.archived`

Where:
- `EnumAlias` is a constant name you choose (e.g. `ArticleStatus`)
- `enumKey` comes from `LocalizableEnum#getKey()` (e.g. `draft`, `published`)

---

## Language files

Example structure:

```
src/main/resources/
  Server/
    Languages/
      en-EN/
        server.lang
      pl-PL/
        server.lang
```

Then you can load them with:

```java
.addLangFilePattern("Server/Languages/%s/server.lang")
```

`%s` will be replaced by the locale tag, e.g. `en-EN`, `pl-PL`.

### Example content (properties-style)

`src/main/resources/Server/Languages/en-EN/server.lang`

```properties
ArticleStatus.draft=Draft
ArticleStatus.published=Published
ArticleStatus.archived=Archived
```

`src/main/resources/Server/Languages/pl-PL/server.lang`

```properties
ArticleStatus.draft=Szkic
ArticleStatus.published=Opublikowany
ArticleStatus.archived=Zarchiwizowany
```

> Use whatever `.lang` format your mod setup expects. The important part is that keys match what you request in code.

---

## Example: a localizable enum

```java
package com.stachu.hytalefactory;

import com.stachu.i18n.LocalizableEnum;

import java.util.Objects;

public enum ArticleStatus implements LocalizableEnum {
    DRAFT("draft"),
    PUBLISHED("published"),
    ARCHIVED("archived");

    private final String key;

    ArticleStatus(String key) {
        this.key = Objects.requireNonNull(key, "key");
        if (key.isBlank()) throw new IllegalArgumentException("key cannot be blank");
    }

    @Override
    public String getKey() {
        return key;
    }
}
```

---

## Example: using it in a Hytale plugin

This example:
1. Builds a `MessageSource` for your mod
2. Creates an `EnumLocalizer`
3. Creates `EnumSupport` for your enum
4. Prints the localized label for a chosen locale

```java
package com.stachu.hytalefactory;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.stachu.i18n.EnumLocalizer;
import com.stachu.i18n.EnumSupport;
import com.stachu.i18n.I18n;
import com.stachu.i18n.MessageSource;

import java.util.Locale;

public class ExamplePlugin extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public ExamplePlugin(JavaPluginInit init) {
        super(init);
        LOGGER.atInfo().log("Hello from %s version %s",
                this.getName(),
                this.getManifest().getVersion().toString());
    }

    @Override
    protected void setup() {
        // Build a MessageSource for your mod
        MessageSource messages = I18n.forMod(ExamplePlugin.class)
                .addLangFilePattern("Server/Languages/%s/server.lang")
                .defaultLocale(Locale.forLanguageTag("en-EN"))
                .build();

        // Localizer for enums
        EnumLocalizer enumLocalizer = new EnumLocalizer(messages);

        // Support for a specific enum
        EnumSupport<ArticleStatus> statuses = EnumSupport.of(ArticleStatus.class, enumLocalizer);

        Locale pl = Locale.forLanguageTag("pl-PL");

        // Get a localized label
        String label = statuses.label(ArticleStatus.PUBLISHED, pl);
        //or
        String label = statuses.label('published', pl);

        LOGGER.atInfo().log("ArticleStatus.PUBLISHED (pl-PL) => %s", label);
    }
}
```

---

## Troubleshooting

### Missing translations / unexpected fallback
- Verify the locale folder exists (e.g. `Server/Languages/pl-PL/server.lang`)
- Verify keys match your convention (`ArticleStatus.published`)
- If a key is missing for `pl-PL`, the library should fall back to the `defaultLocale(...)`
---
