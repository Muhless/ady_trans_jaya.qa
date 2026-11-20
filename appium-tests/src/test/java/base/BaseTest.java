package base;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import pages.LoginPage;

import java.io.File;
import java.net.URL;
import java.time.Duration;

public abstract class BaseTest {

    protected AndroidDriver driver;

    @BeforeEach
    void setUp() {
        String apk = new File("../app/build/outputs/apk/debug/app-debug.apk").getAbsolutePath();

        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName("Android")
                .setDeviceName("emulator-5554")
                .setApp(apk)
                .setAutoGrantPermissions(true)
                .setNewCommandTimeout(Duration.ofSeconds(60));

        URL serverUrl = AppiumServer.start();
        driver = new AndroidDriver(serverUrl, options);

        // Splash punya animasi infinite; idle-wait default Appium bisa mengganggu.
        driver.setSetting("waitForIdleTimeout", 0);

        // Pastikan splash sudah berpindah ke form login sebelum test mulai.
        new LoginPage(driver).waitUntilDisplayed();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}