package tests;

import base.BaseTest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import pages.HomePage;
import pages.LoginPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginTest extends BaseTest {

    @Test
    @DisplayName("halaman login tampil saat aplikasi dibuka")
    void loginPageIsDisplayed() {
        assertTrue(new LoginPage(driver).isDisplayed(), "Form login tidak tampil");
    }

    @Test
    @DisplayName("login dengan data valid menampilkan halaman utama")
    void loginDenganDataValidMenampilkanHalamanUtama() {
        new LoginPage(driver).login(LoginPage.OWNER_EMAIL, LoginPage.OWNER_PASSWORD);

        assertTrue(new HomePage(driver).tampil("Pemilik"),
                "Halaman utama Pemilik tidak tampil setelah login");
    }

    @Test
    @DisplayName("login dengan password salah menampilkan pesan error")
    void wrongPasswordShowsError() {
        LoginPage loginPage = new LoginPage(driver)
                .login(LoginPage.OWNER_EMAIL, "salah123");

        assertTrue(loginPage.errorMenampilkan("Email atau kata sandi salah"),
                () -> "Pesan error tidak sesuai: " + loginPage.errorMessage());
    }

    @Test
    @DisplayName("submit dengan kolom kosong menampilkan pesan error")
    void emptyFieldsShowError() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.tapLogin();

        assertTrue(loginPage.errorMenampilkan("Email dan kata sandi wajib diisi."),
                () -> "Pesan error tidak sesuai: " + loginPage.errorMessage());
    }
}
