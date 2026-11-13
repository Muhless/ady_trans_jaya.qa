package com.adytransjaya.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {

    @Query("SELECT * FROM bookings ORDER BY code DESC")
    fun observeAll(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE customerId = :customerId ORDER BY code DESC")
    fun observeForCustomer(customerId: String): Flow<List<BookingEntity>>

    @Query("SELECT COUNT(*) FROM bookings")
    suspend fun count(): Int

    @Query("SELECT * FROM bookings WHERE code = :code LIMIT 1")
    suspend fun findByCode(code: String): BookingEntity?

    @Query("SELECT * FROM bookings ORDER BY code DESC")
    suspend fun getAll(): List<BookingEntity>

    @Query("SELECT code FROM bookings WHERE code LIKE :prefix || '%' ORDER BY code DESC LIMIT 1")
    suspend fun latestCode(prefix: String): String?

    @Query("SELECT COUNT(*) FROM bookings WHERE carId = :carId AND status IN ('PENDING', 'ACTIVE')")
    suspend fun countActiveForCar(carId: String): Int

    @Query(
        "SELECT COUNT(*) FROM bookings WHERE carId = :carId " +
            "AND status IN ('PENDING', 'ACTIVE') AND code != :excludeCode",
    )
    suspend fun countActiveForCarExcluding(carId: String, excludeCode: String): Int

    @Query("UPDATE bookings SET status = :status WHERE code = :code")
    suspend fun updateStatus(code: String, status: String)

    @Query(
        "UPDATE bookings SET paymentStatus = :paymentStatus, paidAmount = :paidAmount " +
            "WHERE code = :code",
    )
    suspend fun updatePayment(code: String, paymentStatus: String, paidAmount: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(booking: BookingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(bookings: List<BookingEntity>)
}
