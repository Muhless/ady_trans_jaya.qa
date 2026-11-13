package com.adytransjaya.app.ui.login

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MailOutline
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.adytransjaya.app.ui.RegisterUiState

@Composable
fun RegisterScreen(
    state: RegisterUiState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onToggleTerms: () -> Unit,
    onRegister: () -> Unit,
    onBackToLogin: () -> Unit,
) {
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
                title = "Buat akun baru",
                subtitle = "Daftar dan mulai sewa mobil dengan mudah",
                onBack = onBackToLogin,
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
                        top = 26.dp,
                        bottom = 28.dp,
                    ),
                ) {
                    Text(
                        text = "Daftar akun pelanggan",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Isi data berikut untuk membuat akun pelanggan Ady Trans Jaya.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Akun Pemilik dan Karyawan tidak dibuat dari halaman ini. " +
                                    "Hubungi admin jika Anda membutuhkan akses tersebut.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = state.name,
                        onValueChange = onNameChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_name"),
                        label = { Text("Nama lengkap") },
                        placeholder = { Text("Contoh: Andi Pratama") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = null,
                            )
                        },
                        singleLine = true,
                        isError = state.error != null,
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next,
                        ),
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = state.email,
                        onValueChange = onEmailChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_email"),
                        label = { Text("Email") },
                        placeholder = { Text("nama@email.com") },
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
                        value = state.phone,
                        onValueChange = onPhoneChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_phone"),
                        label = { Text("Nomor telepon") },
                        placeholder = { Text("08xxxxxxxxxx") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Phone,
                                contentDescription = null,
                            )
                        },
                        singleLine = true,
                        isError = state.error != null,
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next,
                        ),
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = state.password,
                        onValueChange = onPasswordChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_password"),
                        label = { Text("Kata sandi") },
                        placeholder = { Text("Minimal 6 karakter") },
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
                            imeAction = ImeAction.Next,
                        ),
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = state.confirmPassword,
                        onValueChange = onConfirmPasswordChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_confirm"),
                        label = { Text("Konfirmasi kata sandi") },
                        placeholder = { Text("Ulangi kata sandi") },
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
                        keyboardActions = KeyboardActions(onDone = { onRegister() }),
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onToggleTerms)
                            .testTag("register_terms"),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = state.termsAccepted,
                            onCheckedChange = null,
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Saya setuju dengan Syarat & Ketentuan serta Kebijakan Privasi.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    AnimatedVisibility(visible = state.error != null) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_error"),
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

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onRegister,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("register_submit"),
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
                            Text("Mendaftarkan akun...")
                        } else {
                            Text(
                                text = "Daftar",
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Sudah punya akun?",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        TextButton(
                            onClick = onBackToLogin,
                            modifier = Modifier.testTag("go_to_login"),
                        ) {
                            Text(
                                text = "Masuk",
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Spacer(modifier = Modifier.height(12.dp))

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
}
