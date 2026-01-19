import org.gradle.api.publish.maven.MavenPublication

plugins {
    `java-library`
    `maven-publish`
}

group = providers.gradleProperty("GROUP").orNull ?: "com.stachu"
version = providers.gradleProperty("VERSION_NAME").orNull ?: "0.0.1"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(21)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            pom {
                name.set(providers.gradleProperty("POM_NAME").orNull ?: "hytale-i18n")
                description.set(providers.gradleProperty("POM_DESCRIPTION").orNull ?: "Hytale i18n helper library")
                url.set(providers.gradleProperty("POM_URL").orNull ?: "https://github.com/Sztacho/hytale-i18n")

                licenses {
                    license {
                        name.set(providers.gradleProperty("POM_LICENSE_NAME").orNull ?: "MIT License")
                        url.set(providers.gradleProperty("POM_LICENSE_URL").orNull ?: "https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set(providers.gradleProperty("POM_DEVELOPER_ID").orNull ?: "Sztacho")
                        name.set(providers.gradleProperty("POM_DEVELOPER_NAME").orNull ?: "Sztacho")
                    }
                }

                scm {
                    url.set(providers.gradleProperty("POM_SCM_URL").orNull ?: "https://github.com/Sztacho/hytale-i18n")
                }
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPages"
            url = uri(layout.buildDirectory.dir("maven-repo"))
        }
    }
}
