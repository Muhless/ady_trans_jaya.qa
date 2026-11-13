package com.adytransjaya.app.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adytransjaya.app.ui.home.HomeScreen
import com.adytransjaya.app.ui.login.LoginScreen
import com.adytransjaya.app.ui.login.RegisterScreen
import com.adytransjaya.app.ui.splash.SplashScreen
import kotlinx.coroutines.delay

private const val SPLASH_DURATION_MS = 1500L

@Composable
fun AdyTransJayaApp(viewModel: AppViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Splash hanya tampil sekali per peluncuran activity, bukan setiap
    // kali state login berubah.
    var showSplash by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(SPLASH_DURATION_MS)
        showSplash = false
    }

    AnimatedContent(
        targetState = showSplash,
        transitionSpec = {
            fadeIn(animationSpec = tween(450)) togetherWith
                fadeOut(animationSpec = tween(350))
        },
        label = "splash-transition",
    ) { splashVisible ->
        if (splashVisible) {
            SplashScreen()
        } else {
            AppContent(state = state, viewModel = viewModel)
        }
    }
}

@Composable
private fun AppContent(
    state: AppUiState,
    viewModel: AppViewModel,
) {
    // Target transisi memakai id, bukan objek User, supaya perubahan profil
    // tidak me-recreate HomeScreen dan mengembalikan tab ke Beranda.
    AnimatedContent(
        targetState = state.user?.id,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "auth-transition",
    ) { userId ->
        val user = state.user
        if (userId != null && user != null) {
            HomeScreen(
                user = user,
                onLogout = viewModel::logout,
                onUserUpdated = viewModel::updateSessionUser,
            )
        } else {
            AnimatedContent(
                targetState = state.authScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "auth-screen-transition",
            ) { authScreen ->
                when (authScreen) {
                    AuthScreen.LOGIN -> LoginScreen(
                        state = state.login,
                        onEmailChange = viewModel::onEmailChange,
                        onPasswordChange = viewModel::onPasswordChange,
                        onTogglePassword = viewModel::togglePasswordVisibility,
                        onLogin = viewModel::login,
                        onDemoSelected = viewModel::fillDemoAccount,
                        onRegisterClick = viewModel::showRegister,
                    )

                    AuthScreen.REGISTER -> RegisterScreen(
                        state = state.register,
                        onNameChange = viewModel::onRegisterNameChange,
                        onEmailChange = viewModel::onRegisterEmailChange,
                        onPhoneChange = viewModel::onRegisterPhoneChange,
                        onPasswordChange = viewModel::onRegisterPasswordChange,
                        onConfirmPasswordChange = viewModel::onRegisterConfirmPasswordChange,
                        onTogglePassword = viewModel::toggleRegisterPasswordVisibility,
                        onToggleTerms = viewModel::toggleTermsAccepted,
                        onRegister = viewModel::register,
                        onBackToLogin = viewModel::showLogin,
                    )
                }
            }
        }
    }
}
