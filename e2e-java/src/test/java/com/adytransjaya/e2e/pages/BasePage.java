package com.adytransjaya.e2e.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

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

    protected By byTag(String tag) {
        return AppiumBy.androidUIAutomator(
                "new UiSelector().resourceId(\"" + tag + "\")");
    }

    protected By byText(String text) {
        return AppiumBy.androidUIAutomator(
                "new UiSelector().textContains(\"" + text + "\")");
    }
}
