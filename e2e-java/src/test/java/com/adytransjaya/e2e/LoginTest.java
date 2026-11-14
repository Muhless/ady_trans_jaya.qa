package com.adytransjaya.e2e;

import com.adytransjaya.e2e.pages.HomePage;
import com.adytransjaya.e2e.pages.LoginPage;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * E2E login Ady Trans Jaya memakai Appium Java client + JUnit 5.
 * <p>
 * Setup Appium dan reset aplikasi disediakan oleh BaseTest, sedangkan
 * interaksi UI ada di LoginPage/HomePage.
 */
class LoginTest extends BaseTest {

    @Test
    void menampilkanFormLogin() {
        LoginPage login = loginPage();

        assertTrue(login.formTampil());
        assertTrue(login.tautanDaftarTampil(), "Tautan daftar tidak ditemukan");
    }

    @Test
    void menolakSubmitSaatEmailDanKataSandiKosong() {
        loginPage()
                .kirim()
                .tungguError("Email dan kata sandi wajib diisi.");
    }

    @Test
    void menolakKataSandiYangSalah() {
        LoginPage login = loginPage()
                .isiEmail(LoginPage.OWNER_EMAIL)
                .isiKataSandi("kata-sandi-salah")
                .sembunyikanKeyboard()
                .kirim()
                .tungguError("Email atau kata sandi salah");

        assertTrue(login.masihDiHalamanLogin(), "Seharusnya masih di halaman login");
    }


    @Tag("smoke")
    @Test
    void berhasilMasukSebagaiPemilik() {
        HomePage home = loginPage().masukSebagaiPemilik();

        assertTrue(home.tabTampil("overview"));
    }

    @Tag("smoke")
    @Test
    void berhasilMasukSebagaiKaryawan() {
        HomePage home = loginPage().masukSebagaiKaryawan();
        assertTrue(home.tabTampil("overview"));
    }

    @Tag("smoke")
    @Test
    void berhasilMasukSebagaiPelanggan() {
        HomePage home = loginPage().masukSebagaiPelanggan();
        assertTrue(home.tabTampil("overview"));
    }


    @Test
    void menerimaEmailTanpaMembedakanHurufBesarKecil() {
        loginPage().masuk(
                LoginPage.CUSTOMER_EMAIL.toUpperCase(),
                LoginPage.CUSTOMER_PASSWORD,
                "Pelanggan");
    }

    @Test
    void membukaHalamanRegistrasi() {
        LoginPage register = loginPage()
                .bukaRegistrasi()
                .tungguFormRegistrasi();

        assertTrue(register.tombolDaftarTampil(), "Tombol daftar tidak ditemukan");
    }

    @Test
    void kembaliKeHalamanLoginDariRegistrasi() {
        LoginPage login = loginPage()
                .bukaRegistrasi()
                .tungguFormRegistrasi()
                .kembaliKeLogin();

        assertTrue(login.masihDiHalamanLogin());
    }
}
