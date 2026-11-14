package com.adytransjaya.e2e;

import com.adytransjaya.e2e.pages.ReportPage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * E2E halaman Laporan (tab "Laporan"), hanya untuk role Pemilik.
 * <p>
 * Setup Appium/reset aplikasi ada di BaseTest, interaksi UI di ReportPage.
 */
public class ReportTest extends BaseTest {

    @Test
    void menampilkanHalamanLaporan() {
        loginPage().masukSebagaiPemilik();

        ReportPage report = new ReportPage(driver).open();

        assertTrue(report.halamanTampil());
        report.menampilkanJudul("Laporan & pendapatan");

        String ringkasan = report.jumlahPesananSelesai();
        assertTrue(
                ringkasan.matches("Dihitung dari \\d+ pesanan selesai"),
                "Ringkasan pesanan selesai tidak sesuai: " + ringkasan);

        assertTrue(report.tombolEksporTampil(), "Tombol ekspor CSV tidak ditemukan");

        assertTrue(report.trenTampil(), "Section tren pendapatan tidak ditemukan");
        assertTrue(
                report.pendapatanPerMobilTampil(),
                "Section pendapatan per mobil tidak ditemukan");
        assertTrue(
                report.ringkasanOperasionalTampil(),
                "Section ringkasan operasional tidak ditemukan");
    }

    @Test
    void menampilkanRingkasanOperasional() {
        loginPage().masukSebagaiPemilik();

        ReportPage report = new ReportPage(driver).open()
                .gulirKeRingkasanOperasional();

        report.tungguTeks("Utilisasi armada");
        report.tungguTeks("Pesanan selesai");
        report.tungguTeks("Pesanan dibatalkan");
        report.tungguTeks("Rata-rata durasi sewa");
        report.tungguTeks("Rata-rata per pesanan");
        report.tungguTeks("Pesanan selesai belum lunas");
        report.tungguTeks("Piutang berjalan");
    }

    @Test
    void menampilkanPendapatanPerMobil() {
        loginPage().masukSebagaiPemilik();

        ReportPage report = new ReportPage(driver).open()
                .gulirKePendapatanPerMobil();

        report.tungguTeks("Pendapatan per mobil");
        // Toyota Hiace punya pesanan berstatus selesai pada data contoh.
        report.tungguTeks("Toyota Hiace");
    }

    @Test
    void pelangganTidakMelihatTabLaporan() {
        assertFalse(
                loginPage().masukSebagaiPelanggan().tabTampil("report"),
                "Pelanggan seharusnya tidak melihat tab Laporan");
    }
}
