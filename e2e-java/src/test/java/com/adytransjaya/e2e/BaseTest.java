package com.adytransjaya.e2e;

import com.adytransjaya.e2e.pages.LoginPage;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Base class untuk seluruh E2E test Ady Trans Jaya.
 *
 * Menyiapkan Appium server + driver sekali per test class, lalu mereset
 * aplikasi sebelum setiap test. Interaksi UI ada di Page Object
 * (package {@code com.adytransjaya.e2e.pages}).
 */
public abstract class BaseTest {

    protected static final String APP_ID = "com.adytransjaya.app";

    protected static AndroidDriver driver;

    @BeforeAll
    static void setUpAppium() throws Exception {
        Path apk = apkPath();
        assertTrue(
                Files.isRegularFile(apk),
                "APK tidak ditemukan di " + apk + ". Jalankan dulu: ./gradlew :app:assembleDebug");
        assertTrue(
                hasConnectedDevice(),
                "Tidak ada emulator/perangkat Android yang terhubung. Nyalakan emulator lalu coba lagi.");

        AppiumServer.ensureRunning();

        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName(System.getProperty("android.device", "pixel_api36"))
                .setPlatformVersion(System.getProperty("android.version", "16"))
                .setApp(apk.toString())
                .setAppPackage(APP_ID)
                .setAppActivity(APP_ID + ".MainActivity")
                .setNoReset(false)
                .setAutoGrantPermissions(true)
                .setNewCommandTimeout(Duration.ofSeconds(180));
        options.setCapability("appium:uiautomator2ServerInstallTimeout", 180_000);
        options.setCapability("appium:uiautomator2ServerLaunchTimeout", 180_000);

        driver = new AndroidDriver(
                URI.create("http://" + AppiumServer.HOST + ":" + AppiumServer.PORT + "/").toURL(),
                options);

        // Splash memakai animasi infinite, jadi jangan tunggu UI idle.
        driver.setSetting("waitForIdleTimeout", 0);

        new LoginPage(driver).tungguTampil();
    }

    @AfterAll
    static void tearDownAppium() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
        AppiumServer.stop();
    }

    @BeforeEach
    void resetApp() {
        // Mulai dari data kosong supaya sesi login tidak terbawa.
        driver.executeScript("mobile: terminateApp", Map.of("appId", APP_ID));
        driver.executeScript("mobile: clearApp", Map.of("appId", APP_ID));
        driver.executeScript("mobile: activateApp", Map.of("appId", APP_ID));
        new LoginPage(driver).tungguTampil();
    }

    /** Halaman login yang sudah siap dipakai. */
    protected LoginPage loginPage() {
        return new LoginPage(driver).tungguTampil();
    }

    // ---- Helper environment ----

    private static Path apkPath() {
        String configured = System.getProperty("e2e.apk");
        Path path = configured != null
                ? Path.of(configured)
                : Path.of("..", "app", "build", "outputs", "apk", "debug", "app-debug.apk");
        return path.toAbsolutePath().normalize();
    }

    private static boolean hasConnectedDevice() {
        try {
            Process process = new ProcessBuilder("adb", "devices")
                    .redirectErrorStream(true)
                    .start();
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            process.waitFor(10, TimeUnit.SECONDS);
            return output.lines()
                    .skip(1)
                    .map(String::trim)
                    .anyMatch(line -> line.endsWith("device"));
        } catch (Exception error) {
            return false;
        }
    }
}
