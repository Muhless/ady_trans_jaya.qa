package com.adytransjaya.app.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.adytransjaya.app.data.model.Booking
import com.adytransjaya.app.data.model.BookingStatus
import com.adytransjaya.app.data.model.PaymentStatus

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey val code: String,
    val carId: String,
    val customerId: String,
    val startDate: String,
    val endDate: String,
    val days: Int,
    val total: Long,
    val status: String,
    @ColumnInfo(defaultValue = "UNPAID") val paymentStatus: String = "UNPAID",
    @ColumnInfo(defaultValue = "0") val paidAmount: Long = 0,
) {
    fun toBooking(): Booking = Booking(
        code = code,
        carId = carId,
        customerId = customerId,
        startDate = startDate,
        endDate = endDate,
        days = days,
        total = total,
        status = BookingStatus.valueOf(status),
        paymentStatus = PaymentStatus.valueOf(paymentStatus),
        paidAmount = paidAmount,
    )

    companion object {
        fun from(booking: Booking): BookingEntity = BookingEntity(
            code = booking.code,
            carId = booking.carId,
            customerId = booking.customerId,
            startDate = booking.startDate,
            endDate = booking.endDate,
            days = booking.days,
            total = booking.total,
            status = booking.status.name,
            paymentStatus = booking.paymentStatus.name,
            paidAmount = booking.paidAmount,
        )
    }
}
