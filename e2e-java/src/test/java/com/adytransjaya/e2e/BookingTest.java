package com.adytransjaya.e2e;

import com.adytransjaya.e2e.pages.BookingsPage;
import org.junit.jupiter.api.Test;

/**
 * E2E halaman pesanan (tab "Pesanan").
 *
 * Setup Appium/reset aplikasi ada di BaseTest, interaksi UI di BookingsPage.
 */
class BookingTest extends BaseTest {

    @Test
    void menampilkanHalamanPesanan() {
        loginPage().masukSebagaiPemilik();

        BookingsPage bookings = new BookingsPage(driver).buka();

        bookings.tungguJudul("Daftar pesanan");
        bookings.tungguPesanan("Toyota Innova Zenix");
    }
}
