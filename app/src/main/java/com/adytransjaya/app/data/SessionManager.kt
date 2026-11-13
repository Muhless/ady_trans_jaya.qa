package com.adytransjaya.app.data

import android.content.Context
import com.adytransjaya.app.data.model.Role
import com.adytransjaya.app.data.model.User

/**
 * Menyimpan sesi login agar pengguna tidak perlu masuk ulang
 * setiap kali aplikasi dibuka.
 *
 * Snapshot pengguna disimpan lengkap supaya pemulihan sesi tidak
 * bergantung pada query database yang bersifat asynchronous.
 */
class SessionManager(context: Context) {

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun save(user: User) {
        preferences.edit()
            .putString(KEY_USER_ID, user.id)
            .putString(KEY_NAME, user.name)
            .putString(KEY_EMAIL, user.email)
            .putString(KEY_PHONE, user.phone)
            .putString(KEY_ROLE, user.role.name)
            .putString(KEY_JOINED_AT, user.joinedAt)
            .apply()
    }

    fun currentUser(): User? {
        val id = preferences.getString(KEY_USER_ID, null) ?: return null
        val name = preferences.getString(KEY_NAME, null) ?: return null
        val email = preferences.getString(KEY_EMAIL, null) ?: return null
        val phone = preferences.getString(KEY_PHONE, null) ?: return null
        val roleName = preferences.getString(KEY_ROLE, null) ?: return null
        val joinedAt = preferences.getString(KEY_JOINED_AT, null) ?: return null
        val role = runCatching { Role.valueOf(roleName) }.getOrNull() ?: return null

        return User(
            id = id,
            name = name,
            email = email,
            phone = phone,
            role = role,
            joinedAt = joinedAt,
        )
    }

    fun clear() {
        preferences.edit().clear().apply()
    }

    fun hasSession(): Boolean = preferences.contains(KEY_USER_ID)

    fun savedRole(): Role? = preferences
        .getString(KEY_ROLE, null)
        ?.let { runCatching { Role.valueOf(it) }.getOrNull() }

    private companion object {
        const val PREFS_NAME = "ady_trans_jaya_session"
        const val KEY_USER_ID = "user_id"
        const val KEY_NAME = "name"
        const val KEY_EMAIL = "email"
        const val KEY_PHONE = "phone"
        const val KEY_ROLE = "role"
        const val KEY_JOINED_AT = "joined_at"
    }
}
