package pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object halaman login.
 *
 * Catatan: testTag Compose dipetakan menjadi resource-id tanpa awalan package.
 * AppiumBy.id tidak bisa menemukannya, jadi locator memakai UiSelector
 * resourceId secara langsung.
 */
public class LoginPage {

    private static final Duration TIMEOUT = Duration.ofSeconds(20);

    public static final String OWNER_EMAIL = "owner@adytransjaya.id";
    public static final String OWNER_PASSWORD = "owner123";

    private final AndroidDriver driver;
    private final WebDriverWait wait;

    private final By emailField = byTestTag("login_email");
    private final By passwordField = byTestTag("login_password");
    private final By submitButton = byTestTag("login_submit");
    private final By errorBox = byTestTag("login_error");
    // Teks error ada di TextView di dalam Surface login_error, bukan di Surface-nya.
    private final By errorText = AppiumBy.androidUIAutomator(
            "new UiSelector().resourceId(\"login_error\")"
                    + ".childSelector(new UiSelector().className(\"android.widget.TextView\"))");

    public LoginPage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, TIMEOUT);
    }

    /** Menunggu splash selesai dan form login siap dipakai. */
    public LoginPage waitUntilDisplayed() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailField));
        wait.until(ExpectedConditions.visibilityOfElementLocated(submitButton));
        return this;
    }

    public boolean isDisplayed() {
        try {
            waitUntilDisplayed();
            return true;
        } catch (TimeoutException error) {
            return false;
        }
    }

    public LoginPage enterEmail(String email) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(emailField));
        field.click();
        field.clear();
        field.sendKeys(email);
        return this;
    }

    public LoginPage enterPassword(String password) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField));
        field.click();
        field.clear();
        field.sendKeys(password);
        return this;
    }

    public LoginPage hideKeyboard() {
        try {
            driver.hideKeyboard();
        } catch (Exception ignored) {
            // Keyboard memang tidak sedang tampil.
        }
        return this;
    }

    public void tapLogin() {
        hideKeyboard();
        wait.until(ExpectedConditions.elementToBeClickable(submitButton)).click();
    }

    public LoginPage login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        tapLogin();
        return this;
    }

    public String errorMessage() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(errorBox));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorText)).getText();
    }

    public boolean errorMenampilkan(String pesan) {
        try {
            return errorMessage().contains(pesan);
        } catch (TimeoutException error) {
            return false;
        }
    }

    public static By byTestTag(String tag) {
        return AppiumBy.androidUIAutomator(
                "new UiSelector().resourceId(\"" + tag + "\")");
    }
}
