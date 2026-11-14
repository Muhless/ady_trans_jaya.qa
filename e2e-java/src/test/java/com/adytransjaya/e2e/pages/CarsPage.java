package com.adytransjaya.e2e.pages;

import io.appium.java_client.android.AndroidDriver;

/**
 * Tab Mobil (HomeTab.CARS).
 * <p>
 * Untuk owner/employee halaman ini berjudul "Kelola mobil" dan punya
 * tombol tambah. Untuk customer berjudul "Katalog mobil".
 */
public class CarsPage extends HomePage {

    public CarsPage(AndroidDriver driver) {
        super(driver);
    }

    public CarsPage open() {
        openTab("cars");
        waitVisible(byTag("car_search"));
        return this;
    }

    public boolean halamanTampil() {
        return exists("car_search");
    }

    public boolean tombolTambahTampil() {
        return exists("add_car");
    }

    public CarsPage tungguJudul(String judul) {
        waitForText(judul);
        return this;
    }

    public CarsPage tungguMobil(String namaAtauPlat) {
        waitForText(namaAtauPlat);
        return this;
    }

    public CarsPage cari(String kataKunci) {
        type("car_search", kataKunci);
        return this;
    }
}
