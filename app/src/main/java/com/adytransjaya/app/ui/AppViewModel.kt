package com.adytransjaya.app.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.adytransjaya.app.data.AccountRepository
import com.adytransjaya.app.data.SessionManager
import com.adytransjaya.app.data.UserRepository
import com.adytransjaya.app.data.local.AppDatabase
import com.adytransjaya.app.data.model.Role
import com.adytransjaya.app.data.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AuthScreen {
    LOGIN,
    REGISTER,
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val passwordVisible: Boolean = false,
    val termsAccepted: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)

data class AppUiState(
    val user: User? = null,
    val authScreen: AuthScreen = AuthScreen.LOGIN,
    val login: LoginUiState = LoginUiState(),
    val register: RegisterUiState = RegisterUiState(),
)

class AppViewModel(
    private val sessionManager: SessionManager,
    private val accountRepository: AccountRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AppUiState(user = sessionManager.currentUser()),
    )
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    // ---- Login ----

    fun onEmailChange(value: String) {
        _uiState.update {
            it.copy(login = it.login.copy(email = value, error = null))
        }
    }

    fun onPasswordChange(value: String) {
        _uiState.update {
            it.copy(login = it.login.copy(password = value, error = null))
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update {
            it.copy(login = it.login.copy(passwordVisible = !it.login.passwordVisible))
        }
    }

    fun fillDemoAccount(role: Role) {
        val account = UserRepository.demoAccounts.firstOrNull { it.user.role == role } ?: return
        _uiState.update {
            it.copy(
                login = it.login.copy(
                    email = account.user.email,
                    password = account.password,
                    error = null,
                ),
            )
        }
    }

    fun login() {
        val login = _uiState.value.login
        if (login.isLoading) return

        if (login.email.isBlank() || login.password.isBlank()) {
            _uiState.update {
                it.copy(
                    login = it.login.copy(
                        error = "Email dan kata sandi wajib diisi.",
                    ),
                )
            }
            return
        }

        _uiState.update {
            it.copy(login = it.login.copy(isLoading = true, error = null))
        }

        viewModelScope.launch {
            // Jeda kecil supaya transisi terasa natural.
            delay(400)
            val user = accountRepository.authenticate(login.email, login.password)

            if (user == null) {
                _uiState.update {
                    it.copy(
                        login = it.login.copy(
                            isLoading = false,
                            error = "Email atau kata sandi salah. Gunakan akun demo di bawah.",
                        ),
                    )
                }
            } else {
                sessionManager.save(user)
                _uiState.value = AppUiState(user = user)
            }
        }
    }

    // ---- Register ----

    fun showRegister() {
        _uiState.update {
            it.copy(
                authScreen = AuthScreen.REGISTER,
                register = RegisterUiState(),
            )
        }
    }

    fun showLogin() {
        _uiState.update {
            it.copy(
                authScreen = AuthScreen.LOGIN,
                login = it.login.copy(error = null),
                register = RegisterUiState(),
            )
        }
    }

    fun onRegisterNameChange(value: String) {
        _uiState.update {
            it.copy(register = it.register.copy(name = value, error = null))
        }
    }

    fun onRegisterEmailChange(value: String) {
        _uiState.update {
            it.copy(register = it.register.copy(email = value, error = null))
        }
    }

    fun onRegisterPhoneChange(value: String) {
        _uiState.update {
            it.copy(register = it.register.copy(phone = value, error = null))
        }
    }

    fun onRegisterPasswordChange(value: String) {
        _uiState.update {
            it.copy(register = it.register.copy(password = value, error = null))
        }
    }

    fun onRegisterConfirmPasswordChange(value: String) {
        _uiState.update {
            it.copy(register = it.register.copy(confirmPassword = value, error = null))
        }
    }

    fun toggleRegisterPasswordVisibility() {
        _uiState.update {
            it.copy(
                register = it.register.copy(
                    passwordVisible = !it.register.passwordVisible,
                ),
            )
        }
    }

    fun toggleTermsAccepted() {
        _uiState.update {
            it.copy(
                register = it.register.copy(
                    termsAccepted = !it.register.termsAccepted,
                    error = null,
                ),
            )
        }
    }

    fun register() {
        val register = _uiState.value.register
        if (register.isLoading) return

        val validationError = validateRegistration(register)
        if (validationError != null) {
            _uiState.update {
                it.copy(register = it.register.copy(error = validationError))
            }
            return
        }

        _uiState.update {
            it.copy(register = it.register.copy(isLoading = true, error = null))
        }

        viewModelScope.launch {
            delay(500)
            val result = accountRepository.register(
                name = register.name,
                email = register.email,
                phone = register.phone,
                password = register.password,
            )

            result.fold(
                onSuccess = { user ->
                    sessionManager.save(user)
                    _uiState.value = AppUiState(user = user)
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            register = it.register.copy(
                                isLoading = false,
                                error = error.message
                                    ?: "Pendaftaran gagal. Silakan coba lagi.",
                            ),
                        )
                    }
                },
            )
        }
    }

    fun logout() {
        sessionManager.clear()
        _uiState.value = AppUiState(authScreen = AuthScreen.LOGIN)
    }

    /** Dipanggil setelah profil diubah agar snapshot sesi ikut diperbarui. */
    fun updateSessionUser(user: User) {
        sessionManager.save(user)
        _uiState.update { it.copy(user = user) }
    }

    private fun validateRegistration(state: RegisterUiState): String? {
        if (state.name.trim().length < 3) {
            return "Nama lengkap minimal 3 karakter."
        }

        val emailPattern = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        if (!emailPattern.matches(state.email.trim())) {
            return "Format email belum valid."
        }

        val phoneDigits = state.phone.filter { it.isDigit() }
        if (phoneDigits.length !in 10..15) {
            return "Nomor telepon harus terdiri dari 10–15 digit."
        }

        if (state.password.length < 6) {
            return "Kata sandi minimal 6 karakter."
        }

        if (state.password != state.confirmPassword) {
            return "Konfirmasi kata sandi belum sama."
        }

        if (!state.termsAccepted) {
            return "Centang persetujuan syarat & ketentuan terlebih dahulu."
        }

        return null
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        private val appContext = context.applicationContext
        private val sessionManager = SessionManager(appContext)
        private val accountRepository by lazy {
            AccountRepository(AppDatabase.get(appContext).userDao())
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AppViewModel(sessionManager, accountRepository) as T
        }
    }
}
