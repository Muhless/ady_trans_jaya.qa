package com.adytransjaya.e2e.pages;

import io.appium.java_client.android.AndroidDriver;

/**
 * Halaman login.
 * <p>
 * Method aksi mengembalikan page yang sama (fluent) supaya alur test
 * terbaca sebagai satu rangkaian langkah.
 */
public class LoginPage extends BasePage {

    public static final String OWNER_EMAIL = "owner@adytransjaya.id";
    public static final String OWNER_PASSWORD = "owner123";

    public static final String EMPLOYEE_EMAIL = "karyawan@adytransjaya.id";
    public static final String EMPLOYEE_PASSWORD = "karyawan123";
    public static final String CUSTOMER_EMAIL = "customer@adytransjaya.id";
    public static final String CUSTOMER_PASSWORD = "customer123";

    public LoginPage(AndroidDriver driver) {
        super(driver);
    }

    public LoginPage tungguTampil() {
        waitVisible(byTag("login_email"));
        return this;
    }

    public boolean formTampil() {
        return element("login_email").isDisplayed()
                && element("login_password").isDisplayed()
                && element("login_submit").isDisplayed();
    }

    public boolean tautanDaftarTampil() {
        return exists("go_to_register");
    }

    public LoginPage isiEmail(String email) {
        type("login_email", email);
        return this;
    }

    public LoginPage isiKataSandi(String password) {
        type("login_password", password);
        return this;
    }

    public LoginPage sembunyikanKeyboard() {
        // Tutup keyboard agar tombol Masuk tidak tertutup IME.
        try {
            driver.hideKeyboard();
        } catch (Exception ignored) {
            // Keyboard memang tidak sedang tampil.
        }
        return this;
    }

    public LoginPage kirim() {
        click("login_submit");
        return this;
    }

    public HomePage masuk(String email, String password, String roleLabel) {
        isiEmail(email)
                .isiKataSandi(password)
                .sembunyikanKeyboard()
                .kirim();
        return new HomePage(driver).tungguMasuk(roleLabel);
    }

    public HomePage masukSebagaiPemilik() {
        return masuk(OWNER_EMAIL, OWNER_PASSWORD, "Pemilik");
    }

    public HomePage masukSebagaiKaryawan() {
        return masuk(EMPLOYEE_EMAIL, EMPLOYEE_PASSWORD, "Karyawan");
    }

    public HomePage masukSebagaiPelanggan() {
        return masuk(CUSTOMER_EMAIL, CUSTOMER_PASSWORD, "Pelanggan");
    }

    public LoginPage tungguError(String pesan) {
        waitVisible(byTag("login_error"));
        waitForText(pesan);
        return this;
    }

    public boolean masihDiHalamanLogin() {
        return exists("login_email");
    }

    public LoginPage bukaRegistrasi() {
        click("go_to_register");
        return this;
    }

    public LoginPage tungguFormRegistrasi() {
        waitVisible(byTag("register_email"));
        return this;
    }

    public boolean tombolDaftarTampil() {
        return exists("register_submit");
    }

    public LoginPage kembaliKeLogin() {
        click("back_to_login");
        return tungguTampil();
    }
}
