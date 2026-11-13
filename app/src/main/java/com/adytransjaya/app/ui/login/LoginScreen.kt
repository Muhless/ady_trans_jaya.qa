package com.adytransjaya.app.ui.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MailOutline
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adytransjaya.app.data.UserRepository
import com.adytransjaya.app.data.model.Role
import com.adytransjaya.app.ui.LoginUiState
import com.adytransjaya.app.ui.common.roleContainerColor
import com.adytransjaya.app.ui.common.roleContentColor
import com.adytransjaya.app.ui.common.roleIcon

@Composable
fun LoginScreen(
    state: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onLogin: () -> Unit,
    onDemoSelected: (Role) -> Unit,
    onRegisterClick: () -> Unit,
) {
    var showForgotPassword by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .imePadding(),
        ) {
            AuthHero(
                title = "Ady Trans Jaya",
                subtitle = "Rental mobil nyaman untuk setiap perjalanan",
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                color = MaterialTheme.colorScheme.background,
            ) {
                Column(
                    modifier = Modifier.padding(
                        start = 24.dp,
                        end = 24.dp,
                        top = 28.dp,
                        bottom = 28.dp,
                    ),
                ) {
                    Text(
                        text = "Masuk ke akun Anda",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Gunakan akun pemilik, karyawan, atau pelanggan untuk melanjutkan.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    OutlinedTextField(
                        value = state.email,
                        onValueChange = onEmailChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_email"),
                        label = { Text("Email") },
                        placeholder = { Text("nama@adytransjaya.id") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.MailOutline,
                                contentDescription = null,
                            )
                        },
                        singleLine = true,
                        isError = state.error != null,
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next,
                        ),
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = state.password,
                        onValueChange = onPasswordChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password"),
                        label = { Text("Kata sandi") },
                        placeholder = { Text("Masukkan kata sandi") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Lock,
                                contentDescription = null,
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = onTogglePassword) {
                                Icon(
                                    imageVector = if (state.passwordVisible) {
                                        Icons.Rounded.VisibilityOff
                                    } else {
                                        Icons.Rounded.Visibility
                                    },
                                    contentDescription = if (state.passwordVisible) {
                                        "Sembunyikan kata sandi"
                                    } else {
                                        "Tampilkan kata sandi"
                                    },
                                )
                            }
                        },
                        singleLine = true,
                        isError = state.error != null,
                        shape = RoundedCornerShape(14.dp),
                        visualTransformation = if (state.passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(onDone = { onLogin() }),
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        TextButton(onClick = { showForgotPassword = true }) {
                            Text(
                                text = "Lupa kata sandi?",
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }

                    AnimatedVisibility(visible = state.error != null) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_error"),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.errorContainer,
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ErrorOutline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(20.dp),
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = state.error.orEmpty(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onLogin,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("login_submit"),
                        enabled = !state.isLoading,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                        ),
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Memeriksa akun...")
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Masuk",
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Belum punya akun?",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        TextButton(
                            onClick = onRegisterClick,
                            modifier = Modifier.testTag("go_to_register"),
                        ) {
                            Text(
                                text = "Daftar sekarang",
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outlineVariant,
                        )
                        Text(
                            text = "AKUN DEMO",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.8.sp,
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.outlineVariant,
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tekan salah satu peran untuk mengisi email dan kata sandi otomatis.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    UserRepository.demoAccounts.forEach { account ->
                        DemoAccountCard(
                            role = account.user.role,
                            email = account.user.email,
                            password = account.password,
                            onClick = { onDemoSelected(account.user.role) },
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "© 2026 Ady Trans Jaya · Rental Mobil Terpercaya",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }

    if (showForgotPassword) {
        AlertDialog(
            onDismissRequest = { showForgotPassword = false },
            title = { Text("Lupa kata sandi?") },
            text = {
                Text(
                    "Silakan hubungi admin Ady Trans Jaya di 0812-3456-7890 " +
                        "atau datang langsung ke kantor untuk mengatur ulang kata sandi.",
                )
            },
            confirmButton = {
                TextButton(onClick = { showForgotPassword = false }) {
                    Text("Mengerti")
                }
            },
        )
    }
}

@Composable
private fun DemoAccountCard(
    role: Role,
    email: String,
    password: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("demo_${role.name.lowercase()}")
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        roleContainerColor(role),
                        RoundedCornerShape(12.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = roleIcon(role),
                    contentDescription = null,
                    tint = roleContentColor(role),
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = role.label,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "Sandi: $password",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                imageVector = Icons.Rounded.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
