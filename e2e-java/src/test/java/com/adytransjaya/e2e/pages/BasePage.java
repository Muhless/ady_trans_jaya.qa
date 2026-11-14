package com.adytransjaya.e2e.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Dasar untuk semua Page Object.
 *
 * Hanya berisi aksi UI mentah (cari, klik, ketik). Assertion bisnis
 * tetap dilakukan di class test supaya alur test mudah dibaca.
 */
public abstract class BasePage {

    protected static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);

    protected final AndroidDriver driver;

    protected BasePage(AndroidDriver driver) {
        this.driver = driver;
    }

    protected WebElement waitVisible(By by) {
        WebElement found = new WebDriverWait(driver, DEFAULT_TIMEOUT)
                .until(ExpectedConditions.visibilityOfElementLocated(by));
        assertNotNull(found);
        return found;
    }

    protected WebElement element(String tag) {
        return driver.findElement(byTag(tag));
    }

    protected boolean exists(String tag) {
        return exists(byTag(tag));
    }

    protected boolean exists(By by) {
        return !driver.findElements(by).isEmpty();
    }

    protected void click(String tag) {
        element(tag).click();
    }

    protected void type(String tag, String text) {
        WebElement field = waitVisible(byTag(tag));
        field.click();
        field.clear();
        field.sendKeys(text);
    }

    protected void waitForText(String text) {
        waitVisible(byText(text));
    }

    protected By byExactText(String text) {
        return AppiumBy.androidUIAutomator(
                "new UiSelector().text(\"" + text + "\")");
    }

    protected By byClass(String className, int index) {
        return AppiumBy.androidUIAutomator(
                "new UiSelector().className(\"" + className + "\").instance(" + index + ")");
    }

    protected void clickExactText(String text) {
        waitVisible(byExactText(text)).click();
    }

    protected WebElement scrollToExactText(String text) {
        return scrollTo(byExactText(text));
    }

    protected WebElement scrollToClass(String className, int index) {
        return scrollTo(byClass(className, index));
    }

    /**
     * Scroll halaman ke bawah sampai elemen dengan testTag tertentu
     * terlihat. Diperlukan untuk item LazyColumn yang belum di-compose.
     */
    protected WebElement scrollToTag(String tag) {
        return scrollTo(byTag(tag));
    }

    protected WebElement scrollToText(String text) {
        return scrollTo(byText(text));
    }

    protected By byTag(String tag) {
        return AppiumBy.androidUIAutomator(
                "new UiSelector().resourceId(\"" + tag + "\")");
    }

    protected By byText(String text) {
        return AppiumBy.androidUIAutomator(
                "new UiSelector().textContains(\"" + text + "\")");
    }

    // ---- Helper internal ----

    private WebElement scrollTo(By by) {
        for (int attempt = 0; attempt < 8; attempt++) {
            List<WebElement> found = driver.findElements(by);
            if (!found.isEmpty()) {
                try {
                    if (found.get(0).isDisplayed()) {
                        return found.get(0);
                    }
                } catch (StaleElementReferenceException ignored) {
                    // Elemen diganti saat list di-render ulang; coba lagi.
                }
            }
            scrollDown();
        }
        return waitVisible(by);
    }

    private void scrollDown() {
        Dimension size = driver.manage().window().getSize();
        int left = size.getWidth() / 8;
        int width = size.getWidth() * 3 / 4;
        int top = size.getHeight() / 4;
        int height = size.getHeight() / 3;

        driver.executeScript("mobile: scrollGesture", Map.of(
                "left", left,
                "top", top,
                "width", width,
                "height", height,
                "direction", "down",
                "percent", 0.8));

        try {
            Thread.sleep(300);
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
        }
    }
}
