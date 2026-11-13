package com.adytransjaya.app.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.adytransjaya.app.data.AccountRepository
import com.adytransjaya.app.data.RentalRepository
import com.adytransjaya.app.data.local.AppDatabase
import com.adytransjaya.app.data.model.Booking
import com.adytransjaya.app.data.model.BookingStatus
import com.adytransjaya.app.data.model.Car
import com.adytransjaya.app.data.model.PaymentStatus
import com.adytransjaya.app.data.model.Role
import com.adytransjaya.app.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val cars: List<Car> = emptyList(),
    val bookings: List<Booking> = emptyList(),
    val customers: List<User> = emptyList(),
    val employees: List<User> = emptyList(),
    val isLoading: Boolean = true,
    val message: String? = null,
    val updatedUser: User? = null,
)

/**
 * Menyediakan data mobil, pesanan, dan pelanggan dari Room untuk seluruh tab
 * Home, sekaligus menangani CRUD mobil, alur pesanan, dan profil pengguna.
 */
class HomeViewModel(
    private val rentalRepository: RentalRepository,
    private val accountRepository: AccountRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            rentalRepository.ensureSeeded()
            accountRepository.ensureDemoAccounts()
            combine(
                rentalRepository.cars,
                rentalRepository.bookings,
                accountRepository.customers,
                accountRepository.employees,
            ) { cars, bookings, customers, employees ->
                HomeData(cars, bookings, customers, employees)
            }.collect { data ->
                _uiState.update {
                    it.copy(
                        cars = data.cars,
                        bookings = data.bookings,
                        customers = data.customers,
                        employees = data.employees,
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun saveCar(car: Car, isNew: Boolean) {
        viewModelScope.launch {
            rentalRepository.saveCar(car, isNew)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            message = if (isNew) {
                                "Mobil berhasil ditambahkan."
                            } else {
                                "Mobil berhasil diperbarui."
                            },
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(message = error.message ?: "Gagal menyimpan mobil.")
                    }
                }
        }
    }

    fun deleteCar(carId: String) {
        viewModelScope.launch {
            rentalRepository.deleteCar(carId)
                .onSuccess {
                    _uiState.update { it.copy(message = "Mobil berhasil dihapus.") }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(message = error.message ?: "Gagal menghapus mobil.")
                    }
                }
        }
    }

    fun updatePaymentStatus(code: String, newStatus: PaymentStatus, actorRole: Role) {
        viewModelScope.launch {
            rentalRepository.updatePaymentStatus(
                code = code,
                newStatus = newStatus,
                actorRole = actorRole,
            ).onSuccess {
                val message = when (newStatus) {
                    PaymentStatus.UNPAID -> "Pembayaran ditandai belum bayar."
                    PaymentStatus.DOWN_PAYMENT -> "DP berhasil dicatat."
                    PaymentStatus.PAID -> "Pembayaran lunas dicatat."
                }
                _uiState.update { it.copy(message = message) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(message = error.message ?: "Gagal mencatat pembayaran.")
                }
            }
        }
    }

    fun createBooking(        carId: String,
        customerId: String,
        startEpochDay: Long,
        endEpochDay: Long,
    ) {
        viewModelScope.launch {
            rentalRepository.createBooking(
                carId = carId,
                customerId = customerId,
                startEpochDay = startEpochDay,
                endEpochDay = endEpochDay,
            ).onSuccess {
                _uiState.update {
                    it.copy(
                        message = "Pesanan berhasil dibuat dan menunggu konfirmasi.",
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(message = error.message ?: "Gagal membuat pesanan.")
                }
            }
        }
    }

    fun updateBookingStatus(
        code: String,
        newStatus: BookingStatus,
        actorRole: Role,
        actorId: String,
    ) {
        viewModelScope.launch {
            rentalRepository.updateBookingStatus(
                code = code,
                newStatus = newStatus,
                actorRole = actorRole,
                actorId = actorId,
            ).onSuccess {
                val message = when (newStatus) {
                    BookingStatus.ACTIVE -> "Pesanan berhasil disetujui."
                    BookingStatus.COMPLETED ->
                        "Pesanan selesai. Mobil kembali tersedia."

                    BookingStatus.CANCELLED -> "Pesanan berhasil dibatalkan."
                    BookingStatus.PENDING -> "Status pesanan diperbarui."
                }
                _uiState.update { it.copy(message = message) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(message = error.message ?: "Gagal memperbarui pesanan.")
                }
            }
        }
    }

    fun updateProfile(userId: String, name: String, phone: String) {
        viewModelScope.launch {
            accountRepository.updateProfile(userId, name, phone)
                .onSuccess { updated ->
                    _uiState.update {
                        it.copy(
                            updatedUser = updated,
                            message = "Profil berhasil diperbarui.",
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(message = error.message ?: "Gagal memperbarui profil.")
                    }
                }
        }
    }

    fun changePassword(userId: String, currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            accountRepository.changePassword(userId, currentPassword, newPassword)
                .onSuccess {
                    _uiState.update { it.copy(message = "Kata sandi berhasil diubah.") }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(message = error.message ?: "Gagal mengubah kata sandi.")
                    }
                }
        }
    }

    fun createEmployee(
        name: String,
        email: String,
        phone: String,
        password: String,
    ) {
        viewModelScope.launch {
            accountRepository.createEmployee(
                name = name,
                email = email,
                phone = phone,
                password = password,
            ).onSuccess {
                _uiState.update { it.copy(message = "Akun karyawan berhasil dibuat.") }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(message = error.message ?: "Gagal membuat akun karyawan.")
                }
            }
        }
    }

    fun setEmployeeActive(userId: String, active: Boolean) {
        viewModelScope.launch {
            accountRepository.setEmployeeActive(userId, active)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            message = if (active) {
                                "Akun karyawan diaktifkan."
                            } else {
                                "Akun karyawan dinonaktifkan."
                            },
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(message = error.message ?: "Gagal memperbarui akun karyawan.")
                    }
                }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    fun clearUpdatedUser() {
        _uiState.update { it.copy(updatedUser = null) }
    }

    private data class HomeData(
        val cars: List<Car>,
        val bookings: List<Booking>,
        val customers: List<User>,
        val employees: List<User>,
    )

    class Factory(private val context: Context) : ViewModelProvider.Factory {        private val appContext = context.applicationContext

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val database = AppDatabase.get(appContext)
            return HomeViewModel(
                rentalRepository = RentalRepository(
                    carDao = database.carDao(),
                    bookingDao = database.bookingDao(),
                ),
                accountRepository = AccountRepository(database.userDao()),
            ) as T
        }
    }
}
