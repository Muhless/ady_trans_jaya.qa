package com.adytransjaya.app.data.model

enum class CarStatus(val label: String) {
    AVAILABLE("Tersedia"),
    RENTED("Disewakan"),
    MAINTENANCE("Perawatan"),
}

data class Car(
    val id: String,
    val name: String,
    val brand: String,
    val plate: String,
    val seats: Int,
    val transmission: String,
    val pricePerDay: Long,
    val status: CarStatus,
    val year: Int,
)

enum class BookingStatus(val label: String) {
    PENDING("Menunggu"),
    ACTIVE("Aktif"),
    COMPLETED("Selesai"),
    CANCELLED("Dibatalkan"),
}

enum class PaymentStatus(val label: String) {
    UNPAID("Belum bayar"),
    DOWN_PAYMENT("DP"),
    PAID("Lunas"),
}

data class Booking(
    val code: String,
    val carId: String,
    val customerId: String,
    val startDate: String,
    val endDate: String,
    val days: Int,
    val total: Long,
    val status: BookingStatus,
    val paymentStatus: PaymentStatus = PaymentStatus.UNPAID,
    val paidAmount: Long = 0,
)

data class MonthlyRevenue(
    val month: String,
    val amount: Long,
)
