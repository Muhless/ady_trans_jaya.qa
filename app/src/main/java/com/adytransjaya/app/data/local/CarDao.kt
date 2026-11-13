package com.adytransjaya.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CarDao {

    @Query("SELECT * FROM cars ORDER BY name ASC")
    fun observeAll(): Flow<List<CarEntity>>

    @Query("SELECT * FROM cars WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): CarEntity?

    @Query("SELECT COUNT(*) FROM cars")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM cars WHERE plate = :plate AND id != :excludeId")
    suspend fun countByPlate(plate: String, excludeId: String): Int

    @Query("UPDATE cars SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(car: CarEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cars: List<CarEntity>)

    @Update
    suspend fun update(car: CarEntity)

    @Query("DELETE FROM cars WHERE id = :id")
    suspend fun deleteById(id: String)
}
