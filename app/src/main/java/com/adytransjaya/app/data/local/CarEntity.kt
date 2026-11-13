package com.adytransjaya.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.adytransjaya.app.data.model.Car
import com.adytransjaya.app.data.model.CarStatus

@Entity(tableName = "cars")
data class CarEntity(
    @PrimaryKey val id: String,
    val name: String,
    val brand: String,
    val plate: String,
    val seats: Int,
    val transmission: String,
    val pricePerDay: Long,
    val status: String,
    val year: Int,
) {
    fun toCar(): Car = Car(
        id = id,
        name = name,
        brand = brand,
        plate = plate,
        seats = seats,
        transmission = transmission,
        pricePerDay = pricePerDay,
        status = CarStatus.valueOf(status),
        year = year,
    )

    companion object {
        fun from(car: Car): CarEntity = CarEntity(
            id = car.id,
            name = car.name,
            brand = car.brand,
            plate = car.plate,
            seats = car.seats,
            transmission = car.transmission,
            pricePerDay = car.pricePerDay,
            status = car.status.name,
            year = car.year,
        )
    }
}
