package com.adytransjaya.e2e.pages;

import io.appium.java_client.android.AndroidDriver;

/**
 * Tab Laporan (HomeTab.REPORT).
 * <p>
 * Hanya tersedia untuk role Pemilik. Section bawah berada di dalam
 * LazyColumn, jadi perlu scroll sebelum bisa diverifikasi.
 */
public class ReportPage extends HomePage {

    public ReportPage(AndroidDriver driver) {
        super(driver);
    }

    public ReportPage open() {
        // HomeTab.REPORT menghasilkan testTag "tab_report".
        openTab("report");
        waitForText("Laporan & pendapatan");
        return this;
    }

    public boolean halamanTampil() {
        return exists(byText("Laporan & pendapatan"));
    }

    public ReportPage menampilkanJudul(String judul) {
        waitForText(judul);
        return this;
    }

    public String jumlahPesananSelesai() {
        return waitVisible(byText("Dihitung dari")).getText();
    }

    public boolean tombolEksporTampil() {
        return exists("report_export");
    }

    public ReportPage gulirKeTren() {
        scrollToText("Tren pendapatan 6 bulan");
        return this;
    }

    public ReportPage gulirKePendapatanPerMobil() {
        scrollToText("Pendapatan per mobil");
        return this;
    }

    public ReportPage gulirKeRingkasanOperasional() {
        scrollToText("Ringkasan operasional");
        return this;
    }

    public boolean trenTampil() {
        return scrollToText("Tren pendapatan 6 bulan").isDisplayed();
    }

    public boolean pendapatanPerMobilTampil() {
        return scrollToText("Pendapatan per mobil").isDisplayed();
    }

    public boolean ringkasanOperasionalTampil() {
        return scrollToText("Ringkasan operasional").isDisplayed();
    }

    public ReportPage gulirKeTeks(String teks) {
        scrollToText(teks);
        return this;
    }

    public ReportPage tungguTeks(String teks) {
        scrollToText(teks);
        return this;
    }
}
