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

        cars.tungguMobil("Daihatsu Terios");
        cars.tungguMobil("F 2345 ADY");
    }

    @Test
    void menampilkanHalamanDetailMobil() {
        loginPage().masukSebagaiPemilik();

        CarsPage cars = new CarsPage(driver).open();

        cars.openDetailCars("Toyota Hiace");
        assertTrue(cars.halamanDetailTampil());
    }

    @Test
    void berhasilMenambahMobilBaru() {
        loginPage().masukSebagaiPemilik();

        CarsPage cars = new CarsPage(driver).open();

        cars.bukaFormTambah()
                .isiNama("Toyota Fortuner")
                .isiMerek("Toyota")
                .isiPlat("B 9999 ADY")
                .isiKursi("7")
                .isiTahun("2025")
                .isiHarga("500000")
                .sembunyikanKeyboard()
                .pilihTransmisi("Manual")
                .pilihStatus("available")
                .simpan();

        // Data baru harus langsung muncul di daftar.
        cars.tungguMobil("Toyota Fortuner");
        cars.tungguMobil("B 9999 ADY");
    }

    @Test
    void mengubahDataMobil() {
        loginPage().masukSebagaiPemilik();

        CarsPage cars = new CarsPage(driver).open();

        cars.openDetailCars("Toyota Avanza")
                .bukaEditDariDetail()
                .isiHarga("400000")
                .sembunyikanKeyboard()
                .simpanPerubahan();

        // Tetap di halaman detail dan harga sudah berubah.
        cars.tungguDetailMobil("Toyota Avanza");
        cars.tungguMobil("Rp 400.000");
    }

    @Test
    void menghapusDataMobil() {
        loginPage().masukSebagaiPemilik();

        CarsPage cars = new CarsPage(driver).open();

        cars.openDetailCars("Toyota Hiace");
        assertTrue(cars.halamanDetailTampil());

        cars.bukaHapusDariDetail().konfirmasiHapus();

        cars.tungguUnitTerdaftar(4);
        cars.tungguMobil("Daihatsu Terios");
        assertTrue(!cars.mobilTampil("Toyota Hiace"), "Cars should been deleted");
    }


}
