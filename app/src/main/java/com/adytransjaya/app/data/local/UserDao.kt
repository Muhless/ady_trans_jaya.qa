package com.adytransjaya.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE role = 'CUSTOMER' ORDER BY name ASC")
    fun observeCustomers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE role = 'EMPLOYEE' ORDER BY name ASC")
    fun observeEmployees(): Flow<List<UserEntity>>

    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    suspend fun countByEmail(email: String): Int

    @Query("UPDATE users SET name = :name, phone = :phone WHERE id = :id")
    suspend fun updateProfile(id: String, name: String, phone: String)

    @Query("UPDATE users SET passwordHash = :hash, passwordSalt = :salt WHERE id = :id")
    suspend fun updatePassword(id: String, hash: String, salt: String)

    @Query("UPDATE users SET isActive = :isActive WHERE id = :id")
    suspend fun updateActive(id: String, isActive: Boolean)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(user: UserEntity)
}
