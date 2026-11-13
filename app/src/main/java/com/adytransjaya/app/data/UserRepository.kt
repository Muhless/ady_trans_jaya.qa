package com.adytransjaya.app.data

import com.adytransjaya.app.data.model.User

object UserRepository {

    data class DemoAccount(
        val user: User,
        val password: String,
    )

    /** Akun demo yang ditampilkan juga di halaman login. */
    val demoAccounts: List<DemoAccount> = listOf(
        DemoAccount(DemoUsers.owner, "owner123"),
        DemoAccount(DemoUsers.employee, "karyawan123"),
        DemoAccount(DemoUsers.andi, "customer123"),
    )

    /**
     * Autentikasi lokal. Pencocokan email tidak membedakan huruf besar/kecil.
     * Kembalikan null bila kredensial tidak cocok.
     */
    fun authenticate(email: String, password: String): User? {
        val normalizedEmail = email.trim()
        return demoAccounts
            .firstOrNull {
                it.user.email.equals(normalizedEmail, ignoreCase = true) &&
                    it.password == password
            }
            ?.user
    }

    fun findById(id: String): User? =
        demoAccounts.map { it.user }.firstOrNull { it.id == id }
}
