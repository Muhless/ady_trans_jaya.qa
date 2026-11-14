# ady_trans_jaya

Aplikasi rental mobil **Ady Trans Jaya** untuk Android, dengan tiga peran pengguna:
**Pemilik**, **Karyawan**, dan **Pelanggan**.

Aplikasi dibuat dengan **Kotlin + Jetpack Compose + Material 3**. Seluruh source
ada di folder `app/`, dan aplikasi dijalankan **secara manual** lewat Gradle atau
Android Studio — tanpa scaffold automation/Appium.

---

## Aplikasi Android

### Fitur yang sudah tersedia

- **Splash screen** ber-branding Ady Trans Jaya saat aplikasi dibuka, lalu
  otomatis beralih ke login atau dashboard (jika sesi masih aktif).
- **Login multi-role** dengan validasi email/kata sandi.
- **Registrasi pelanggan** — akun baru disimpan permanen di database lokal **Room**
  dengan kata sandi yang di-hash (SHA-256 + salt per akun), bukan teks biasa.
- **Sesi login tersimpan** — pengguna tidak perlu login ulang saat aplikasi dibuka kembali.
- **Akun demo** yang bisa diisi otomatis hanya dengan menekan kartu peran.
- **Tampilan dashboard berbeda per peran**:
  - **Pemilik** — ringkasan pendapatan, statistik armada, pesanan, laporan 6 bulan, pendapatan per mobil, profil.
  - **Karyawan** — mobil tersedia, pesanan perlu konfirmasi, daftar pesanan, data pelanggan, profil.
  - **Pelanggan** — pesanan aktif, riwayat perjalanan, katalog mobil, pesanan saya, profil.
- **Kelola mobil (khusus Pemilik & Karyawan)** — tambah, edit, dan hapus unit
  lewat dialog formulir dengan validasi (nama, merek, plat unik, kursi, tahun,
  harga, transmisi, status). Pelanggan hanya bisa melihat katalog.
- **Proteksi hapus mobil** — unit yang masih punya pesanan `Menunggu`/`Aktif`
  tidak bisa dihapus; pesan alasannya ditampilkan lewat snackbar.
- **Alur pesan mobil untuk Pelanggan** — tombol **Buat pesanan** membuka dialog
  untuk memilih mobil `Tersedia`, menentukan tanggal mulai/selesai lewat date
  picker, melihat durasi & total otomatis, lalu menyimpan pesanan berstatus
  `Menunggu`. Tanggal lampau, tanggal selesai sebelum mulai, dan bentrok jadwal
  mobil yang sama ditolak dengan pesan yang jelas.
- **Aksi status pesanan** — Pemilik/Karyawan dapat **Setujui** (`Menunggu` →
  `Aktif`), **Selesaikan** (`Aktif` → `Selesai`), dan **Batalkan**; Pelanggan
  hanya bisa membatalkan pesanannya sendiri yang masih `Menunggu`.
- **Detail pesanan (invoice)** — tekan kartu pesanan untuk melihat rincian
  pelanggan, mobil, jadwal, harga per hari, total, dan penjelasan status.
  Staf bisa langsung menekan **Hubungi** untuk membuka aplikasi telepon dengan
  nomor pelanggan.
- **Status pembayaran** — setiap pesanan punya status bayar `Belum bayar`,
  `DP` (50% dari total), atau `Lunas`, lengkap dengan jumlah yang sudah dibayar
  dan sisa tagihan. Staf mencatatnya lewat tombol **Catat DP 50%** dan
  **Tandai lunas** di detail pesanan; pelanggan hanya bisa melihat. Pesanan yang
  dibatalkan tidak bisa dibayar.
- **Estimasi denda keterlambatan** — pesanan `Aktif` yang melewati tanggal
  selesai menampilkan kartu peringatan berisi jumlah hari terlambat dan estimasi
  denda 10% dari harga harian per hari (informasi saja, tidak disimpan).
- **Laporan dihitung dari transaksi selesai** — pendapatan bulan berjalan,
  tren 6 bulan, pertumbuhan dibanding bulan lalu, pendapatan per mobil, rata-rata
  durasi sewa, rata-rata pendapatan per pesanan, jumlah pesanan selesai yang belum
  lunas, dan **piutang berjalan** berasal dari pesanan berstatus `Selesai`, bukan
  angka statis.
- **Ekspor laporan CSV** — tombol **Ekspor CSV** di tab Laporan menulis rincian
  pesanan selesai (kode, tanggal, mobil, pelanggan, total, dibayar, sisa, status)
  ke file CSV di cache, lalu membuka lembar berbagi Android agar file bisa
  disimpan atau dikirim ke aplikasi lain.
- **Kelola akun karyawan (khusus Pemilik)** — buat akun karyawan baru dari menu
  Profil dan aktifkan/nonaktifkan akun. Karyawan nonaktif tidak bisa masuk,
  tetapi data dan riwayat pesanannya tetap tersimpan.
- **Sinkronisasi status mobil** — menyetujui pesanan mengubah mobil menjadi
  `Disewakan`, sedangkan menyelesaikan/membatalkan mengembalikannya ke
  `Tersedia` bila tidak ada pesanan aktif lain untuk mobil tersebut.
- **Edit profil & ganti kata sandi** — nama dan nomor telepon bisa diubah;
  kata sandi baru wajib memverifikasi kata sandi lama dan disimpan sebagai
  hash + salt baru. Snapshot sesi ikut diperbarui tanpa logout.
- **Data armada, pesanan, dan pelanggan tersimpan permanen di Room** — perubahan
  mobil, pesanan, profil, dan seluruh statistik dashboard (Beranda, Pesanan,
  Laporan, Pelanggan) langsung membaca database, jadi tetap ada setelah
  aplikasi ditutup.
- **Logout** dengan konfirmasi.
- Tema terang/gelap mengikuti pengaturan perangkat.

### Akun demo

| Peran | Email | Kata sandi |
|---|---|---|
| Pemilik | `owner@adytransjaya.id` | `owner123` |
| Karyawan | `karyawan@adytransjaya.id` | `karyawan123` |
| Pelanggan | `customer@adytransjaya.id` | `customer123` |

Di halaman login, cukup tekan kartu **Pemilik / Karyawan / Pelanggan** pada bagian
**Akun Demo**, lalu tekan **Masuk**.

Ketiga akun demo (beserta pelanggan contoh) juga di-seed ke Room, sehingga nama,
nomor telepon, dan kata sandinya bisa diubah lewat menu **Profil** seperti akun
biasa.

### Registrasi pelanggan

Halaman registrasi dibuka lewat tautan **"Belum punya akun? Daftar sekarang"** di
bawah tombol masuk. Data yang diminta:

- nama lengkap,
- email,
- nomor telepon,
- kata sandi dan konfirmasi kata sandi,
- persetujuan Syarat & Ketentuan.

Setelah berhasil mendaftar, pengguna langsung masuk sebagai **Pelanggan**.
Akun **Pemilik** dan **Karyawan** tidak dibuat dari halaman ini (dibuat oleh admin).

### Database lokal (Room)

Aplikasi memakai **Room** (lapisan ORM di atas SQLite) sebagai database lokal.
Room dipilih karena type-safe, terintegrasi dengan coroutines/ViewModel, dan
mendukung migrasi skema.

- Nama database: `ady_trans_jaya.db`
- Skema versi **4**:
  - `users` (id, name, email, phone, passwordHash, passwordSalt, role, joinedAt,
    isActive) — email unik (indeks unik pada kolom `email`); `isActive` menandai
    akun karyawan yang dinonaktifkan Pemilik
  - `cars` (id, name, brand, plate, seats, transmission, pricePerDay, status, year)
  - `bookings` (code, carId, customerId, startDate, endDate, days, total, status,
    paymentStatus, paidAmount)
- Ekspor skema: `app/schemas/` (`1.json`, `2.json`, `3.json`, `4.json`)
- Migrasi `1 → 2` menambah tabel `cars` dan `bookings` **tanpa menghapus akun**
  yang sudah terdaftar.
- Migrasi `2 → 3` menambah kolom `isActive` (default aktif) pada tabel `users`.
- Migrasi `3 → 4` menambah kolom `paymentStatus` dan `paidAmount` pada tabel
  `bookings`; pesanan lama yang sudah `Selesai` diisi `Lunas` sebesar totalnya
  supaya laporan tetap konsisten.
- Kata sandi: `SHA-256(salt + kata sandi)` dengan salt acak per pengguna.
  Untuk produksi, autentikasi sebaiknya ditangani backend dengan bcrypt/Argon2.

Data awal (6 mobil dan 7 pesanan contoh) di-seed **sekali** oleh
`RentalRepository.ensureSeeded()` saat tabel masih kosong. Akun demo dan
pelanggan contoh di-seed oleh `AccountRepository.ensureDemoAccounts()` bila belum
ada. Setelah itu seluruh perubahan dilakukan lewat UI dan tersimpan di Room.
Autentikasi memeriksa Room lebih dulu, lalu jatuh ke kredensial demo sebagai
cadangan. Sesi login disimpan sebagai snapshot di SharedPreferences
(`SessionManager`) supaya pemulihan sesi tetap sinkron.

### Alur pesanan

Kode pesanan dibuat otomatis dengan format `BK-<tahun>-<nomor>` (contoh
`BK-2026-015`). Tanggal disimpan sebagai teks `dd MMM yyyy` dan dibandingkan
lewat *epoch day* (`RentalDates`), sehingga pengecekan bentrok jadwal tidak
bergantung pada format tampilan.

| Peran | Transisi status yang diizinkan |
|---|---|
| Pemilik / Karyawan | `Menunggu → Aktif`, `Menunggu → Dibatalkan`, `Aktif → Selesai`, `Aktif → Dibatalkan` |
| Pelanggan | `Menunggu → Dibatalkan` (hanya pesanan miliknya) |

Saat pesanan disetujui, mobil berstatus `Disewakan`. Saat pesanan selesai atau
dibatalkan, mobil kembali `Tersedia` apabila tidak ada pesanan `Menunggu`/`Aktif`
lain pada mobil yang sama.

### Alur pembayaran

Pembayaran dicatat manual oleh Pemilik/Karyawan dari detail pesanan. Nominalnya
mengikuti status yang dipilih:

| Status | Label UI | Nominal tersimpan |
|---|---|---|
| `UNPAID` | Belum bayar | `0` |
| `DOWN_PAYMENT` | DP | 50% dari total (minimal Rp 1) |
| `PAID` | Lunas | seluruh total |

Pesanan berstatus `Dibatalkan` tidak bisa diubah status bayarnya, dan pelanggan
tidak punya akses untuk mengubah pembayaran. Pesanan contoh yang sudah `Selesai`
otomatis dianggap lunas. Tab Laporan memakai `total` sebagai pendapatan dan
menghitung **piutang berjalan** dari `total - paidAmount` pesanan selesai.

### Menjalankan aplikasi (manual)

Prasyarat: JDK 17+, Android SDK, dan emulator/perangkat Android.

```bash
cd /home/muhless/Coding/android/ady_trans_jaya

# 1. (Opsional) nyalakan emulator jika belum aktif
~/Android/Sdk/emulator/emulator -avd pixel_api36 &

# 2. Build APK debug
./gradlew :app:assembleDebug

# 3. Pasang ke emulator/perangkat
adb install -r app/build/outputs/apk/debug/app-debug.apk

# 4. Jalankan aplikasi
adb shell am start -n com.adytransjaya.app/.MainActivity
```

Atau buka folder project ini di **Android Studio**, tunggu Gradle sync, pilih
device, lalu klik **Run ▶** pada konfigurasi `app`.

APK hasil build:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Ingin mulai dari database kosong (seed ulang 6 mobil + 7 pesanan)?

```bash
adb shell pm clear com.adytransjaya.app
adb shell am start -n com.adytransjaya.app/.MainActivity
```

### Struktur aplikasi

```text
app/src/main/java/com/adytransjaya/app/
├── MainActivity.kt
├── data/
│   ├── AccountRepository.kt     # autentikasi Room, registrasi, profil, karyawan
│   ├── DemoData.kt              # data contoh untuk seed mobil & pesanan
│   ├── DemoUsers.kt             # akun contoh
│   ├── PasswordHasher.kt        # hash SHA-256 + salt
│   ├── RentalDates.kt          # parse/format tanggal pesanan (epoch day)
│   ├── RentalRepository.kt      # seed, CRUD mobil, alur pesanan & status
│   ├── SessionManager.kt        # sesi login (SharedPreferences)
│   ├── UserRepository.kt        # autentikasi akun demo
│   ├── local/
│   │   ├── AppDatabase.kt       # singleton Room v4 + migrasi 1→2, 2→3, 3→4
│   │   ├── BookingDao.kt        # query pesanan
│   │   ├── BookingEntity.kt     # tabel bookings
│   │   ├── CarDao.kt            # query mobil
│   │   ├── CarEntity.kt         # tabel cars
│   │   ├── UserDao.kt           # query akun
│   │   └── UserEntity.kt        # tabel users
│   └── model/                   # User, Role, Car, Booking
└── ui/
    ├── AppViewModel.kt          # state login, registrasi & sesi
    ├── AdyTransJayaApp.kt       # transisi login/register ↔ dashboard
    ├── common/                  # komponen & format Rupiah
    ├── login/
    │   ├── AuthHero.kt          # header bersama login & register
    │   ├── LoginScreen.kt       # tampilan login
    │   └── RegisterScreen.kt    # tampilan registrasi pelanggan
    ├── splash/
    │   └── SplashScreen.kt      # layar pembuka ber-branding
    ├── home/
    │   ├── HomeScreen.kt        # navigasi bawah per role
    │   ├── HomeViewModel.kt     # data Room + aksi mobil, pesanan, profil
    │   └── tabs/                # Beranda, Mobil, Pesanan, Laporan, Pelanggan, Profil
    │       ├── BookingDetailDialog.kt # rincian pesanan + hubungi pelanggan
    │       ├── BookingFormDialog.kt  # form pesan mobil + date picker
    │       ├── EmployeeDialogs.kt    # kelola akun karyawan (Pemilik)
    │       └── ProfileDialogs.kt     # edit profil & ganti kata sandi
    └── theme/                   # warna & tipografi
```

### Catatan pengembangan

- Data mobil dan pesanan disimpan di **Room**. `data/DemoData.kt` sekarang hanya
  dipakai untuk **seed awal** (6 mobil + 7 pesanan) ketika tabel masih kosong.
- Akun hasil registrasi **disimpan permanen di Room**; akun demo dan pelanggan
  contoh di-seed ke Room agar profilnya bisa diedit. Autentikasi terpusat di
  `AccountRepository` sehingga nanti bisa diganti dengan API tanpa mengubah
  halaman login/dashboard.
- Perubahan data armada, pesanan, dan profil terpusat di `RentalRepository` /
  `AccountRepository` + `HomeViewModel`, dan semua tab Home membaca `StateFlow`
  yang sama sehingga statistik ikut ter-update.
- Halaman login, registrasi, kelola mobil, pesanan, dan profil sudah memakai
  `testTag` (mis. `login_email`, `login_submit`, `go_to_register`,
  `register_email`, `register_submit`, `add_car`, `car_form_submit`,
  `car_edit_<id>`, `car_delete_<id>`, `car_delete_confirm`, `add_booking`,
  `booking_form_submit`, `booking_approve_<kode>`, `booking_complete_<kode>`,
  `booking_cancel_<kode>`, `booking_detail_contact`, `booking_payment_dp`,
  `booking_payment_paid`, `profile_edit`, `profile_change_password`,
  `employee_manage`, `employee_add`, `employee_form_submit`, `report_export`)
  untuk memudahkan pengujian manual dan otomasi di masa depan.
- Ekspor CSV memakai `FileProvider` (`${applicationId}.fileprovider`) dengan
  folder yang diizinkan di `res/xml/file_paths.xml` (`cache/exports/`), sehingga
  file laporan bisa dibagikan tanpa menyimpan data ke penyimpanan publik.

---

## Build & test aplikasi

```bash
# Build APK
./gradlew :app:assembleDebug

# Unit test Kotlin (folder app/src/test, saat ini masih berupa stub kosong)
./gradlew :app:testDebugUnitTest

# E2E login memakai Appium Java client + JUnit 5
./gradlew :e2e-java:test
```

Hasil unit test ada di `app/build/reports/tests/testDebugUnitTest/index.html`,
sedangkan hasil E2E ada di `e2e-java/build/reports/tests/test/index.html`.

### E2E login (Java)

Prasyarat:

- Emulator/perangkat Android menyala dan terdeteksi (`adb devices`).
- APK debug sudah dibuild (`./gradlew :app:assembleDebug`).
- Perintah `appium` tersedia di `PATH`. Server Appium dijalankan otomatis oleh
  test di `127.0.0.1:4723` (dan dimatikan lagi setelah selesai); kalau sudah ada
  server yang berjalan, test memakainya.

Test berada di `e2e-java/src/test/java/com/adytransjaya/e2e/` dan mencakup
tampilan form, validasi input kosong, kata sandi salah, login pemilik, email
tanpa membedakan huruf besar/kecil, serta navigasi ke/dari halaman registrasi.
Interaksi UI memakai Page Object Model di `e2e-java/src/test/java/com/adytransjaya/e2e/pages/`
(`LoginPage`, `HomePage`, `CarsPage`, `BookingsPage`), sedangkan `BaseTest`
menangani setup Appium dan reset aplikasi.
Setiap test memulai ulang aplikasi dengan data bersih
(`mobile: terminateApp` + `mobile: clearApp` + `mobile: activateApp`), jadi APK
tidak perlu di-install ulang. Log server Appium ditulis ke
`e2e-java/build/appium-server.log`.
