package tests;

import base.BaseTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppLaunchTest extends BaseTest {

    @Test
    @DisplayName("aplikasi terbuka dengan package yang benar")
    void appOpensWithCorrectPackage() {
        assertEquals("com.adytransjaya.app", driver.getCurrentPackage());
    }
}