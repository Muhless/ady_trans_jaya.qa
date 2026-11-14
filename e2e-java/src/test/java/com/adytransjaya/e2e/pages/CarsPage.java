package com.adytransjaya.e2e.pages;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;

import org.openqa.selenium.WebElement;

/**
 * Tab Mobil (HomeTab.CARS).
 * <p>
 * Untuk owner/employee halaman ini berjudul "Kelola mobil" dan punya
 * tombol tambah. Untuk customer berjudul "Katalog mobil".
 */
public class CarsPage extends HomePage {

    private static final String EDIT_TEXT = "android.widget.EditText";
    private static final String CHECK_BOX = "android.widget.CheckBox";

    // Urutan EditText di dialog tambah mobil.
    private static final int FIELD_NAME = 0;
    private static final int FIELD_BRAND = 1;
    private static final int FIELD_PLATE = 2;
    private static final int FIELD_SEATS = 3;
    private static final int FIELD_YEAR = 4;
    private static final int FIELD_PRICE = 5;

    // Urutan CheckBox di dialog tambah mobil (chip FilterChip).
    private static final int CHIP_MATIC = 0;
    private static final int CHIP_MANUAL = 1;
    private static final int CHIP_AVAILABLE = 2;
    private static final int CHIP_RENTED = 3;
    private static final int CHIP_MAINTENANCE = 4;

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
        scrollToText(namaAtauPlat);
        return this;
    }

    public CarsPage cari(String kataKunci) {
        type("car_search", kataKunci);
        return this;
    }

    // ---- Dialog tambah mobil ----

    public CarsPage bukaFormTambah() {
        click("add_car");
        waitVisible(byExactText("Tambah mobil"));
        waitVisible(byClass(EDIT_TEXT, FIELD_NAME));
        return this;
    }


    public CarsPage openDetailCars(String namaMobil) {
        scrollToText(namaMobil).click();
        waitVisible(byTag("car_detail_back"));
        return this;
    }

    public boolean halamanDetailTampil() {
        return exists("car_detail_back");
    }

    public CarsPage tungguDetailMobil(String namaMobil) {
        waitVisible(byTag("car_detail_back"));
        waitForText(namaMobil);
        return this;
    }

    public CarsPage kembaliDariDetail() {
        click("car_detail_back");
        waitVisible(byTag("car_search"));
        return this;
    }

    public CarsPage bukaEditDariDetail() {
        // Tombol "Edit" hanya ada di halaman detail.
        clickExactText("Edit");
        waitVisible(byExactText("Edit mobil"));
        waitVisible(byClass(EDIT_TEXT, FIELD_NAME));
        return this;
    }

    public CarsPage bukaHapusDariDetail() {
        clickExactText("Hapus");
        waitForText("Hapus mobil?");
        return this;
    }

    public CarsPage tungguUnitTerdaftar(int jumlah) {
        waitForText(jumlah + " unit terdaftar");
        return this;
    }

    public boolean mobilTampil(String name) {
        return exists(byText(name));
    }

    /**
     * Konfirmasi hapus. Dialog memakai window terpisah sehingga testTag
     * tidak terekspos; tombol dicari lewat teks "Hapus" di dalam dialog.
     */
    public CarsPage konfirmasiHapus() {
        scrollToExactText("Hapus").click();
        return this;
    }

    public CarsPage tungguPesanHapusDitolak(String pesan) {
        waitForText(pesan);
        return this;
    }

    public CarsPage simpanPerubahan() {
        // Dialog edit memakai tombol "Simpan", bukan "Tambah".
        scrollToExactText("Simpan").click();
        return this;
    }

    public CarsPage isiNama(String nama) {
        return isiField(FIELD_NAME, nama);
    }

    public CarsPage isiMerek(String merek) {
        return isiField(FIELD_BRAND, merek);
    }

    public CarsPage isiPlat(String plat) {
        return isiField(FIELD_PLATE, plat);
    }

    public CarsPage isiKursi(String kursi) {
        return isiField(FIELD_SEATS, kursi);
    }

    public CarsPage isiTahun(String tahun) {
        return isiField(FIELD_YEAR, tahun);
    }

    public CarsPage isiHarga(String harga) {
        return isiField(FIELD_PRICE, harga);
    }

    public CarsPage pilihTransmisi(String transmisi) {
        int chip = "manual".equalsIgnoreCase(transmisi) ? CHIP_MANUAL : CHIP_MATIC;
        scrollToClass(CHECK_BOX, chip).click();
        return this;
    }

    public CarsPage pilihStatus(String status) {
        int chip = switch (status.toLowerCase()) {
            case "rented" -> CHIP_RENTED;
            case "maintenance" -> CHIP_MAINTENANCE;
            default -> CHIP_AVAILABLE;
        };
        scrollToClass(CHECK_BOX, chip).click();
        return this;
    }

    public CarsPage sembunyikanKeyboard() {
        try {
            if (driver.isKeyboardShown()) {
                // Enter memicu ImeAction.Done. Jangan pakai hideKeyboard():
                // BACK dari hideKeyboard ikut menutup dialog.
                driver.pressKey(new KeyEvent(AndroidKey.ENTER));
            }
        } catch (Exception ignored) {
            // Keyboard memang tidak sedang tampil.
        }
        return this;
    }

    public CarsPage simpan() {
        scrollToExactText("Tambah").click();
        return this;
    }

    public CarsPage tungguErrorForm(String pesan) {
        scrollToText(pesan);
        return this;
    }

    // ---- Helper internal ----

    private CarsPage isiField(int index, String value) {
        WebElement field = waitVisible(byClass(EDIT_TEXT, index));
        field.click();
        field.clear();
        field.sendKeys(value);
        return this;
    }
}
