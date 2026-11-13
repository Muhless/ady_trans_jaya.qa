package com.adytransjaya.app.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.adytransjaya.app.data.model.Role
import com.adytransjaya.app.data.model.User

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)],
)
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val passwordHash: String,
    val passwordSalt: String,
    val role: String,
    val joinedAt: String,
    @ColumnInfo(defaultValue = "1") val isActive: Boolean = true,
) {
    fun toUser(): User = User(
        id = id,
        name = name,
        email = email,
        phone = phone,
        role = Role.valueOf(role),
        joinedAt = joinedAt,
        isActive = isActive,
    )
}
