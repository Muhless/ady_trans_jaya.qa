plugins {
    java
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    testImplementation("io.appium:java-client:9.4.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    dependsOn(":app:assembleDebug")
    useJUnitPlatform()

    // E2E bergantung pada emulator dan state aplikasi: hasilnya tidak valid
    // untuk dianggap up-to-date atau dipulihkan dari build cache.
    outputs.upToDateWhen { false }
    outputs.cacheIf { false }

    testLogging {
        events("passed", "failed", "skipped")
    }
}