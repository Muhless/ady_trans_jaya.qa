package com.adytransjaya.e2e.pages;

import io.appium.java_client.android.AndroidDriver;

/**
 * Halaman utama setelah login: top bar dan bottom navigation.
 * <p>
 * Tab dicari lewat testTag "tab_<nama enum lowercase()>", misalnya
 * HomeTab.CARS menjadi "tab_cars".
 */
public class HomePage extends BasePage {

    public HomePage(AndroidDriver driver) {
        super(driver);
    }

    public HomePage openTab(String tab) {
        click("tab_" + tab);
        return this;
    }

    public HomePage tungguMasuk(String roleLabel) {
        waitVisible(byTag("home_root"));
        waitForText("Ady Trans Jaya · " + roleLabel);
        return this;
    }

    public boolean tabTampil(String tab) {
        return exists("tab_" + tab);
    }
}
