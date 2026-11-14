package com.adytransjaya.e2e.pages;

import io.appium.java_client.android.AndroidDriver;

/**
 * Tab Pesanan (HomeTab.BOOKINGS).
 * <p>
 * Judul halaman: "Daftar pesanan" untuk owner/employee dan
 * "Pesanan saya" untuk customer.
 */
public class BookingsPage extends HomePage {

    public BookingsPage(AndroidDriver driver) {
        super(driver);
    }

    public BookingsPage buka() {
        openTab("bookings");
        return this;
    }

    public BookingsPage tungguJudul(String judul) {
        waitForText(judul);
        return this;
    }

    public BookingsPage tungguPesanan(String teks) {
        scrollToText(teks);
        return this;
    }

    public boolean tombolBuatPesananTampil() {
        return exists("add_booking");
    }
}
