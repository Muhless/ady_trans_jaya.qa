package com.adytransjaya.e2e;

import com.adytransjaya.e2e.pages.CarsPage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * E2E halaman mobil (tab "Mobil") memakai Appium Java client + JUnit 5.
 * <p>
 * Setup Appium/reset aplikasi ada di BaseTest, interaksi UI di CarsPage.
 */
class CarTest extends BaseTest {

    @Test
    void menampilkanHalamanKelolaMobil() {
        loginPage().masukSebagaiPemilik();

        CarsPage cars = new CarsPage(driver).open();

        assertTrue(cars.halamanTampil());

        cars.tungguJudul("Kelola mobil");
        assertTrue(cars.tombolTambahTampil(), "Tombol tambah mobil tidak ditemukan");

        // Data armada contoh ikut tampil di daftar.
        cars.tungguMobil("Daihatsu Terios");
        cars.tungguMobil("F 2345 ADY");
    }
}
