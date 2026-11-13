package com.adytransjaya.app.data

import com.adytransjaya.app.data.local.BookingDao
import com.adytransjaya.app.data.local.BookingEntity
import com.adytransjaya.app.data.local.CarDao
import com.adytransjaya.app.data.local.CarEntity
import com.adytransjaya.app.data.model.Booking
import com.adytransjaya.app.data.model.BookingStatus
import com.adytransjaya.app.data.model.Car
import com.adytransjaya.app.data.model.CarStatus
import com.adytransjaya.app.data.model.PaymentStatus
import com.adytransjaya.app.data.model.Role
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

/**
 * Sumber data armada mobil dan pesanan.
 *
 * Data awal (contoh) di-seed sekali ke Room, lalu seluruh UI membaca
 * dari database agar perubahan tetap ada setelah aplikasi ditutup.
 */
class RentalRepository(
    private val carDao: CarDao,
    private val bookingDao: BookingDao,
) {

    val cars: Flow<List<Car>> = carDao.observeAll().map { entities ->
        entities.map(CarEntity::toCar)
    }

    val bookings: Flow<List<Booking>> = bookingDao.observeAll().map { entities ->
        entities.map(BookingEntity::toBooking)
    }

    suspend fun ensureSeeded() {
        if (carDao.count() == 0) {
            carDao.insertAll(DemoData.cars.map(CarEntity::from))
        }
        if (bookingDao.count() == 0) {
            bookingDao.insertAll(
                DemoData.bookings.map { booking ->
                    if (booking.status == BookingStatus.COMPLETED) {
                        booking.copy(
                            paymentStatus = PaymentStatus.PAID,
                            paidAmount = booking.total,
                        )
                    } else {
                        booking
                    }
                }.map(BookingEntity::from),
            )
        }
    }

    suspend fun saveCar(car: Car, isNew: Boolean): Result<Unit> {
        val normalizedPlate = car.plate.trim().uppercase()
        val excludedId = if (isNew) "" else car.id

        if (normalizedPlate.isBlank()) {
            return Result.failure(IllegalArgumentException("Plat nomor wajib diisi."))
        }
        if (carDao.countByPlate(normalizedPlate, excludedId) > 0) {
            return Result.failure(
                IllegalArgumentException("Plat nomor $normalizedPlate sudah terdaftar."),
            )
        }

        val entity = CarEntity.from(
            car.copy(
                name = car.name.trim(),
                brand = car.brand.trim(),
                plate = normalizedPlate,
            ),
        )

        return runCatching {
            if (isNew) {
                carDao.insert(entity)
            } else {
                carDao.update(entity)
            }
        }
    }

    suspend fun deleteCar(carId: String): Result<Unit> {
        val activeCount = bookingDao.countActiveForCar(carId)
        if (activeCount > 0) {
            return Result.failure(
                IllegalStateException(
                    "Mobil tidak bisa dihapus karena masih memiliki " +
                        "$activeCount pesanan aktif atau menunggu.",
                ),
            )
        }
        return runCatching { carDao.deleteById(carId) }
    }

    suspend fun createBooking(
        carId: String,
        customerId: String,
        startEpochDay: Long,
        endEpochDay: Long,
    ): Result<Booking> {
        val carEntity = carDao.findById(carId)
            ?: return Result.failure(IllegalStateException("Mobil tidak ditemukan."))
        val car = carEntity.toCar()

        if (car.status != CarStatus.AVAILABLE) {
            return Result.failure(
                IllegalStateException("Mobil sedang tidak tersedia untuk disewa."),
            )
        }

        val today = RentalDates.todayEpochDay()
        if (startEpochDay < today) {
            return Result.failure(
                IllegalArgumentException("Tanggal mulai tidak boleh di masa lalu."),
            )
        }
        if (endEpochDay <= startEpochDay) {
            return Result.failure(
                IllegalArgumentException("Tanggal selesai harus setelah tanggal mulai."),
            )
        }

        val overlaps = bookingDao.getAll().any { existing ->
            if (existing.carId != carId) return@any false
            val status = runCatching { BookingStatus.valueOf(existing.status) }.getOrNull()
            if (status != BookingStatus.PENDING && status != BookingStatus.ACTIVE) {
                return@any false
            }
            val existingStart = RentalDates.parseEpochDay(existing.startDate)
                ?: return@any false
            val existingEnd = RentalDates.parseEpochDay(existing.endDate)
                ?: return@any false
            startEpochDay < existingEnd && endEpochDay > existingStart
        }
        if (overlaps) {
            return Result.failure(
                IllegalStateException(
                    "Mobil sudah dipesan pada rentang tanggal tersebut. " +
                        "Pilih tanggal atau mobil lain.",
                ),
            )
        }

        val days = (endEpochDay - startEpochDay).toInt()
        val booking = Booking(
            code = nextBookingCode(),
            carId = carId,
            customerId = customerId,
            startDate = RentalDates.formatEpochDay(startEpochDay),
            endDate = RentalDates.formatEpochDay(endEpochDay),
            days = days,
            total = days.toLong() * car.pricePerDay,
            status = BookingStatus.PENDING,
        )

        return runCatching {
            bookingDao.insert(BookingEntity.from(booking))
            booking
        }
    }

    suspend fun updateBookingStatus(
        code: String,
        newStatus: BookingStatus,
        actorRole: Role,
        actorId: String,
    ): Result<Unit> {
        val entity = bookingDao.findByCode(code)
            ?: return Result.failure(IllegalStateException("Pesanan tidak ditemukan."))
        val current = runCatching { BookingStatus.valueOf(entity.status) }.getOrNull()
            ?: return Result.failure(IllegalStateException("Status pesanan tidak valid."))

        val allowed = canTransition(
            current = current,
            next = newStatus,
            actorRole = actorRole,
            isOwnerOfBooking = entity.customerId == actorId,
        )
        if (!allowed) {
            return Result.failure(
                IllegalStateException("Status pesanan tidak dapat diubah."),
            )
        }

        if (newStatus == BookingStatus.ACTIVE) {
            val car = carDao.findById(entity.carId)?.toCar()
                ?: return Result.failure(IllegalStateException("Mobil tidak ditemukan."))
            if (car.status == CarStatus.MAINTENANCE) {
                return Result.failure(
                    IllegalStateException("Mobil sedang dalam perawatan."),
                )
            }
            if (bookingDao.countActiveForCarExcluding(entity.carId, code) > 0) {
                return Result.failure(
                    IllegalStateException("Mobil masih dipakai pesanan lain."),
                )
            }
        }

        return runCatching {
            bookingDao.updateStatus(code, newStatus.name)

            when (newStatus) {
                BookingStatus.ACTIVE -> {
                    carDao.updateStatus(entity.carId, CarStatus.RENTED.name)
                }

                BookingStatus.COMPLETED,
                BookingStatus.CANCELLED,
                -> {
                    val car = carDao.findById(entity.carId)?.toCar()
                    if (
                        car != null &&
                        car.status == CarStatus.RENTED &&
                        bookingDao.countActiveForCar(entity.carId) == 0
                    ) {
                        carDao.updateStatus(entity.carId, CarStatus.AVAILABLE.name)
                    }
                }

                BookingStatus.PENDING -> Unit
            }
        }
    }

    /**
     * Mencatat pembayaran pesanan. Hanya staf yang boleh mengubah status bayar.
     * DP dihitung 50% dari total, lunas berarti seluruh total.
     */
    suspend fun updatePaymentStatus(
        code: String,
        newStatus: PaymentStatus,
        actorRole: Role,
    ): Result<Unit> {
        if (actorRole == Role.CUSTOMER) {
            return Result.failure(
                IllegalStateException("Pembayaran hanya dapat dicatat oleh staf."),
            )
        }

        val entity = bookingDao.findByCode(code)
            ?: return Result.failure(IllegalStateException("Pesanan tidak ditemukan."))
        val bookingStatus = runCatching { BookingStatus.valueOf(entity.status) }.getOrNull()
            ?: return Result.failure(IllegalStateException("Status pesanan tidak valid."))
        if (bookingStatus == BookingStatus.CANCELLED) {
            return Result.failure(
                IllegalStateException("Pesanan yang dibatalkan tidak perlu dibayar."),
            )
        }

        val paidAmount = when (newStatus) {
            PaymentStatus.UNPAID -> 0L
            PaymentStatus.DOWN_PAYMENT -> (entity.total / 2).coerceAtLeast(1L)
            PaymentStatus.PAID -> entity.total
        }

        return runCatching {
            bookingDao.updatePayment(
                code = code,
                paymentStatus = newStatus.name,
                paidAmount = paidAmount,
            )
        }
    }

    private fun canTransition(
        current: BookingStatus,
        next: BookingStatus,
        actorRole: Role,
        isOwnerOfBooking: Boolean,
    ): Boolean = when (actorRole) {
        Role.CUSTOMER ->
            isOwnerOfBooking &&
                current == BookingStatus.PENDING &&
                next == BookingStatus.CANCELLED

        Role.OWNER,
        Role.EMPLOYEE,
        -> when (current) {
            BookingStatus.PENDING ->
                next == BookingStatus.ACTIVE || next == BookingStatus.CANCELLED

            BookingStatus.ACTIVE ->
                next == BookingStatus.COMPLETED || next == BookingStatus.CANCELLED

            BookingStatus.COMPLETED,
            BookingStatus.CANCELLED,
            -> false
        }
    }

    private suspend fun nextBookingCode(): String {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val prefix = "BK-$year-"
        val latest = bookingDao.latestCode(prefix)
        val lastNumber = latest?.substringAfterLast('-')?.toIntOrNull() ?: 0
        return prefix + (lastNumber + 1).toString().padStart(3, '0')
    }
}
