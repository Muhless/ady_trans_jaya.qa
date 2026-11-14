plugins {
    java
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

dependencies {
    testImplementation("io.appium:java-client:10.1.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.14.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.14.4")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform {
        val tags = (findProperty("e2eTags") as String?)?.split(",")
        if (tags != null) includeTags(*tags.toTypedArray())
    }

    dependsOn(":app:assembleDebug")

    val apk = rootProject.layout.projectDirectory
        .file("app/build/outputs/apk/debug/app-debug.apk")
        .asFile
        .absolutePath
    systemProperty("e2e.apk", apk)

    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.SHORT
        showStandardStreams = true
    }
}
