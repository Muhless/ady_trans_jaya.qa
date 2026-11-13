package com.adytransjaya.app.data

import java.security.MessageDigest
import java.util.UUID

/**
 * Hash kata sandi lokal dengan SHA-256 + salt per akun.
 *
 * Catatan: untuk produksi, autentikasi sebaiknya ditangani backend
 * dengan algoritma khusus password seperti bcrypt/Argon2.
 */
object PasswordHasher {

    fun newSalt(): String = UUID.randomUUID().toString()

    fun hash(password: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest("$salt:$password".toByteArray(Charsets.UTF_8))
        return digest.joinToString(separator = "") { byte -> "%02x".format(byte) }
    }

    fun verify(password: String, salt: String, expectedHash: String): Boolean =
        hash(password, salt) == expectedHash
}
