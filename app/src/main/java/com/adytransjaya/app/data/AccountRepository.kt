package com.adytransjaya.app.data

import com.adytransjaya.app.data.local.UserDao
import com.adytransjaya.app.data.local.UserEntity
import com.adytransjaya.app.data.model.Role
import com.adytransjaya.app.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Sumber autentikasi aplikasi:
 * - akun demo ikut di-seed ke Room agar profil dan kata sandinya bisa diubah,
 * - akun baru hasil register disimpan di Room.
 */
class AccountRepository(
    private val userDao: UserDao,
) {

    val customers: Flow<List<User>> = userDao.observeCustomers().map { entities ->
        entities.map(UserEntity::toUser)
    }

    val employees: Flow<List<User>> = userDao.observeEmployees().map { entities ->
        entities.map(UserEntity::toUser)
    }

    suspend fun authenticate(email: String, password: String): User? {
        val normalizedEmail = normalizeEmail(email)
        ensureDemoAccounts()

        val entity = userDao.findByEmail(normalizedEmail)
        if (entity != null) {
            if (!entity.isActive) return null
            val passwordValid = PasswordHasher.verify(
                password = password,
                salt = entity.passwordSalt,
                expectedHash = entity.passwordHash,
            )
            return if (passwordValid) entity.toUser() else null
        }

        // Cadangan bila akun demo belum sempat masuk ke Room.
        return UserRepository.authenticate(normalizedEmail, password)
    }

    suspend fun emailExists(email: String): Boolean {
        val normalizedEmail = normalizeEmail(email)
        val isDemoEmail = UserRepository.demoAccounts.any {
            it.user.email.equals(normalizedEmail, ignoreCase = true)
        }
        return isDemoEmail || userDao.countByEmail(normalizedEmail) > 0
    }

    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
    ): Result<User> {
        val normalizedEmail = normalizeEmail(email)
        if (emailExists(normalizedEmail)) {
            return Result.failure(
                IllegalArgumentException("Email sudah terdaftar. Silakan masuk."),
            )
        }

        val salt = PasswordHasher.newSalt()
        val user = User(
            id = "customer-${UUID.randomUUID()}",
            name = name.trim(),
            email = normalizedEmail,
            phone = normalizePhone(phone),
            role = Role.CUSTOMER,
            joinedAt = currentMonth(),
        )

        return runCatching {
            userDao.insert(user.toEntity(hash = PasswordHasher.hash(password, salt), salt = salt))
            user
        }.recoverCatching { error ->
            if (error is android.database.sqlite.SQLiteConstraintException) {
                throw IllegalArgumentException("Email sudah terdaftar. Silakan masuk.")
            }
            throw error
        }
    }

    /**
     * Membuat akun karyawan baru. Hanya dipanggil dari menu Pemilik.
     */
    suspend fun createEmployee(
        name: String,
        email: String,
        phone: String,
        password: String,
    ): Result<User> {
        val trimmedName = name.trim()
        val trimmedPhone = phone.trim()
        val normalizedEmail = normalizeEmail(email)

        if (trimmedName.length < 3) {
            return Result.failure(IllegalArgumentException("Nama minimal 3 karakter."))
        }
        if (!normalizedEmail.contains('@') || !normalizedEmail.contains('.')) {
            return Result.failure(IllegalArgumentException("Format email tidak valid."))
        }
        if (trimmedPhone.filter(Char::isDigit).length !in 10..15) {
            return Result.failure(
                IllegalArgumentException("Nomor telepon harus terdiri dari 10–15 digit."),
            )
        }
        if (password.length < 6) {
            return Result.failure(
                IllegalArgumentException("Kata sandi minimal 6 karakter."),
            )
        }
        if (emailExists(normalizedEmail)) {
            return Result.failure(
                IllegalArgumentException("Email sudah terdaftar. Gunakan email lain."),
            )
        }

        val salt = PasswordHasher.newSalt()
        val user = User(
            id = "employee-${UUID.randomUUID()}",
            name = trimmedName,
            email = normalizedEmail,
            phone = trimmedPhone,
            role = Role.EMPLOYEE,
            joinedAt = currentMonth(),
        )

        return runCatching {
            userDao.insert(user.toEntity(hash = PasswordHasher.hash(password, salt), salt = salt))
            user
        }.recoverCatching { error ->
            if (error is android.database.sqlite.SQLiteConstraintException) {
                throw IllegalArgumentException("Email sudah terdaftar. Gunakan email lain.")
            }
            throw error
        }
    }

    /** Mengaktifkan atau menonaktifkan akun karyawan. */
    suspend fun setEmployeeActive(userId: String, active: Boolean): Result<Unit> {
        val entity = userDao.findById(userId)
            ?: return Result.failure(IllegalStateException("Akun tidak ditemukan."))
        if (entity.role != Role.EMPLOYEE.name) {
            return Result.failure(
                IllegalStateException("Hanya akun karyawan yang bisa diubah di sini."),
            )
        }
        return runCatching { userDao.updateActive(userId, active) }
    }

    suspend fun updateProfile(
        userId: String,
        name: String,
        phone: String,
    ): Result<User> {        val trimmedName = name.trim()
        val trimmedPhone = phone.trim()
        if (trimmedName.length < 3) {
            return Result.failure(IllegalArgumentException("Nama minimal 3 karakter."))
        }
        val phoneDigits = trimmedPhone.filter(Char::isDigit)
        if (phoneDigits.length !in 10..15) {
            return Result.failure(
                IllegalArgumentException("Nomor telepon harus terdiri dari 10–15 digit."),
            )
        }

        ensureDemoAccounts()
        val entity = userDao.findById(userId)
            ?: return Result.failure(IllegalStateException("Akun tidak ditemukan."))

        return runCatching {
            userDao.updateProfile(id = userId, name = trimmedName, phone = trimmedPhone)
            entity.copy(name = trimmedName, phone = trimmedPhone).toUser()
        }
    }

    suspend fun changePassword(
        userId: String,
        currentPassword: String,
        newPassword: String,
    ): Result<Unit> {
        if (newPassword.length < 6) {
            return Result.failure(
                IllegalArgumentException("Kata sandi baru minimal 6 karakter."),
            )
        }
        if (newPassword == currentPassword) {
            return Result.failure(
                IllegalArgumentException("Kata sandi baru harus berbeda dari yang lama."),
            )
        }

        ensureDemoAccounts()
        val entity = userDao.findById(userId)
            ?: return Result.failure(IllegalStateException("Akun tidak ditemukan."))

        val valid = PasswordHasher.verify(
            password = currentPassword,
            salt = entity.passwordSalt,
            expectedHash = entity.passwordHash,
        )
        if (!valid) {
            return Result.failure(IllegalArgumentException("Kata sandi saat ini salah."))
        }

        val salt = PasswordHasher.newSalt()
        return runCatching {
            userDao.updatePassword(
                id = userId,
                hash = PasswordHasher.hash(newPassword, salt),
                salt = salt,
            )
        }
    }

    /**
     * Memastikan akun demo dan pelanggan contoh tersedia di Room supaya
     * profil bisa diedit dan daftar pelanggan terbaca dari satu sumber.
     */
    suspend fun ensureDemoAccounts() {
        demoProfiles.forEach { (user, password) ->
            val salt = PasswordHasher.newSalt()
            userDao.insertIfAbsent(
                user.toEntity(hash = PasswordHasher.hash(password, salt), salt = salt),
            )
        }
    }

    private fun User.toEntity(hash: String, salt: String): UserEntity = UserEntity(
        id = id,
        name = name,
        email = email,
        phone = phone,
        passwordHash = hash,
        passwordSalt = salt,
        role = role.name,
        joinedAt = joinedAt,
        isActive = isActive,
    )

    private fun normalizeEmail(email: String): String = email.trim().lowercase()

    private fun normalizePhone(phone: String): String = phone.trim()

    private fun currentMonth(): String =
        SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("id-ID")).format(Date())

    private companion object {
        /** Akun demo + pelanggan contoh; kata sandi yang tidak dipakai tetap acak. */
        val demoProfiles: List<Pair<User, String>> = buildList {
            UserRepository.demoAccounts.forEach { add(it.user to it.password) }
            DemoUsers.customers.forEach { customer ->
                if (none { it.first.id == customer.id }) {
                    add(customer to UUID.randomUUID().toString())
                }
            }
        }
    }
}
