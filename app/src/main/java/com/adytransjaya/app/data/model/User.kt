package com.adytransjaya.app.data.model

/**
 * Peran yang bisa masuk ke aplikasi.
 * Satu akun hanya memiliki satu peran, sehingga menu yang tampil
 * otomatis menyesuaikan setelah login.
 */
enum class Role(
    val label: String,
    val shortDescription: String,
    val greeting: String,
) {
    OWNER(
        label = "Pemilik",
        shortDescription = "Akses penuh ke operasional dan laporan",
        greeting = "Pantau bisnis rental Anda hari ini.",
    ),
    EMPLOYEE(
        label = "Karyawan",
        shortDescription = "Kelola mobil, pesanan, dan pelanggan",
        greeting = "Siap melayani pelanggan hari ini?",
    ),
    CUSTOMER(
        label = "Pelanggan",
        shortDescription = "Sewa mobil dan pantau pesanan",
        greeting = "Mau jalan ke mana hari ini?",
    ),
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: Role,
    val joinedAt: String,
    val isActive: Boolean = true,
) {
    val initials: String
        get() = name
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }

    val firstName: String
        get() = name.substringBefore(" ")
}
