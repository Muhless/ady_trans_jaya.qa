package pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/** Halaman utama setelah login berhasil. */
public class HomePage {

    private final WebDriverWait wait;

    public HomePage(AndroidDriver driver) {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public boolean tampil(String roleLabel) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    LoginPage.byTestTag("home_root")));
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    AppiumBy.androidUIAutomator(
                            "new UiSelector().textContains(\"Ady Trans Jaya · "
                                    + roleLabel + "\")")));
            return true;
        } catch (TimeoutException error) {
            return false;
        }
    }
}
