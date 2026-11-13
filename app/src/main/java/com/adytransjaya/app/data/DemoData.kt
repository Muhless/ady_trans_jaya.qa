package com.adytransjaya.app.data

import com.adytransjaya.app.data.model.Booking
import com.adytransjaya.app.data.model.BookingStatus
import com.adytransjaya.app.data.model.Car
import com.adytransjaya.app.data.model.CarStatus
import com.adytransjaya.app.data.model.MonthlyRevenue

/**
 * Data contoh untuk mengisi dashboard. Semua data masih lokal supaya
 * aplikasi bisa langsung dijalankan sebelum backend tersedia.
 */
object DemoData {

    val cars: List<Car> = listOf(
        Car(
            id = "car-001",
            name = "Toyota Avanza",
            brand = "Toyota",
            plate = "B 1234 ADY",
            seats = 7,
            transmission = "Matic",
            pricePerDay = 350_000,
            status = CarStatus.AVAILABLE,
            year = 2024,
        ),
        Car(
            id = "car-002",
            name = "Mitsubishi Xpander",
            brand = "Mitsubishi",
            plate = "B 5678 ADY",
            seats = 7,
            transmission = "Matic",
            pricePerDay = 400_000,
            status = CarStatus.RENTED,
            year = 2023,
        ),
        Car(
            id = "car-003",
            name = "Toyota Innova Zenix",
            brand = "Toyota",
            plate = "D 9012 ADY",
            seats = 7,
            transmission = "Matic",
            pricePerDay = 550_000,
            status = CarStatus.AVAILABLE,
            year = 2025,
        ),
        Car(
            id = "car-004",
            name = "Honda Brio",
            brand = "Honda",
            plate = "B 3456 ADY",
            seats = 5,
            transmission = "Matic",
            pricePerDay = 275_000,
            status = CarStatus.RENTED,
            year = 2024,
        ),
        Car(
            id = "car-005",
            name = "Toyota Hiace",
            brand = "Toyota",
            plate = "B 7890 ADY",
            seats = 15,
            transmission = "Manual",
            pricePerDay = 900_000,
            status = CarStatus.MAINTENANCE,
            year = 2022,
        ),
        Car(
            id = "car-006",
            name = "Daihatsu Terios",
            brand = "Daihatsu",
            plate = "F 2345 ADY",
            seats = 7,
            transmission = "Manual",
            pricePerDay = 375_000,
            status = CarStatus.AVAILABLE,
            year = 2024,
        ),
    )

    val bookings: List<Booking> = listOf(
        Booking(
            code = "BK-2026-011",
            carId = "car-001",
            customerId = "customer-001",
            startDate = "08 Nov 2026",
            endDate = "11 Nov 2026",
            days = 3,
            total = 1_050_000,
            status = BookingStatus.ACTIVE,
        ),
        Booking(
            code = "BK-2026-010",
            carId = "car-002",
            customerId = "customer-002",
            startDate = "06 Nov 2026",
            endDate = "09 Nov 2026",
            days = 3,
            total = 1_200_000,
            status = BookingStatus.COMPLETED,
        ),
        Booking(
            code = "BK-2026-012",
            carId = "car-003",
            customerId = "customer-003",
            startDate = "12 Nov 2026",
            endDate = "15 Nov 2026",
            days = 3,
            total = 1_650_000,
            status = BookingStatus.PENDING,
        ),
        Booking(
            code = "BK-2026-009",
            carId = "car-004",
            customerId = "customer-001",
            startDate = "25 Okt 2026",
            endDate = "27 Okt 2026",
            days = 2,
            total = 550_000,
            status = BookingStatus.COMPLETED,
        ),
        Booking(
            code = "BK-2026-013",
            carId = "car-006",
            customerId = "customer-002",
            startDate = "18 Nov 2026",
            endDate = "20 Nov 2026",
            days = 2,
            total = 750_000,
            status = BookingStatus.PENDING,
        ),
        Booking(
            code = "BK-2026-008",
            carId = "car-005",
            customerId = "customer-003",
            startDate = "10 Okt 2026",
            endDate = "12 Okt 2026",
            days = 2,
            total = 1_800_000,
            status = BookingStatus.COMPLETED,
        ),
        Booking(
            code = "BK-2026-014",
            carId = "car-003",
            customerId = "customer-001",
            startDate = "22 Nov 2026",
            endDate = "24 Nov 2026",
            days = 2,
            total = 1_100_000,
            status = BookingStatus.CANCELLED,
        ),
    )

    val monthlyRevenue: List<MonthlyRevenue> = listOf(
        MonthlyRevenue("Jun", 18_500_000),
        MonthlyRevenue("Jul", 21_200_000),
        MonthlyRevenue("Agu", 24_800_000),
        MonthlyRevenue("Sep", 27_400_000),
        MonthlyRevenue("Okt", 29_600_000),
        MonthlyRevenue("Nov", 32_750_000),
    )

    fun carById(id: String): Car? = cars.firstOrNull { it.id == id }

    fun bookingsForCustomer(customerId: String): List<Booking> =
        bookings.filter { it.customerId == customerId }

    fun customerById(id: String) = DemoUsers.customers.firstOrNull { it.id == id }

    val totalRevenue: Long = monthlyRevenue.sumOf { it.amount }

    val currentMonthRevenue: Long = monthlyRevenue.last().amount
}
