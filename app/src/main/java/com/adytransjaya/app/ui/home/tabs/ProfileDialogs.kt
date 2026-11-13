package com.adytransjaya.app.ui.home.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.adytransjaya.app.data.model.User

@Composable
fun EditProfileDialog(
    user: User,
    onDismiss: () -> Unit,
    onSave: (name: String, phone: String) -> Unit,
) {
    var name by remember(user.id) { mutableStateOf(user.name) }
    var phone by remember(user.id) { mutableStateOf(user.phone) }
    var error by remember { mutableStateOf<String?>(null) }

    fun submit() {
        val validationError = when {
            name.trim().length < 3 -> "Nama minimal 3 karakter."
            phone.filter(Char::isDigit).length !in 10..15 ->
                "Nomor telepon harus terdiri dari 10–15 digit."

            else -> null
        }
        if (validationError != null) {
            error = validationError
            return
        }
        onSave(name.trim(), phone.trim())
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 18.dp),
            ) {
                DialogHeader(
                    title = "Edit profil",
                    subtitle = "Perbarui nama dan nomor telepon Anda.",
                    onDismiss = onDismiss,
                    closeTestTag = "profile_form_close",
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        error = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_form_name"),
                    label = { Text("Nama lengkap") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                        error = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_form_phone"),
                    label = { Text("Nomor telepon") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done,
                    ),
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Email ${user.email} tidak dapat diubah.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                DialogError(error)

                Spacer(modifier = Modifier.height(18.dp))
                DialogButtons(
                    confirmLabel = "Simpan",
                    confirmTestTag = "profile_form_submit",
                    onDismiss = onDismiss,
                    onConfirm = { submit() },
                )
            }
        }
    }
}

@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onSave: (currentPassword: String, newPassword: String) -> Unit,
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    fun submit() {
        val validationError = when {
            currentPassword.isBlank() -> "Kata sandi saat ini wajib diisi."
            newPassword.length < 6 -> "Kata sandi baru minimal 6 karakter."
            newPassword == currentPassword ->
                "Kata sandi baru harus berbeda dari yang lama."

            newPassword != confirmPassword -> "Konfirmasi kata sandi belum sama."
            else -> null
        }
        if (validationError != null) {
            error = validationError
            return
        }
        onSave(currentPassword, newPassword)
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 18.dp),
            ) {
                DialogHeader(
                    title = "Ganti kata sandi",
                    subtitle = "Masukkan kata sandi lama untuk mengonfirmasi.",
                    onDismiss = onDismiss,
                    closeTestTag = "password_form_close",
                )

                Spacer(modifier = Modifier.height(16.dp))

                PasswordField(
                    value = currentPassword,
                    onValueChange = {
                        currentPassword = it
                        error = null
                    },
                    label = "Kata sandi saat ini",
                    testTag = "password_form_current",
                )

                Spacer(modifier = Modifier.height(12.dp))

                PasswordField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        error = null
                    },
                    label = "Kata sandi baru",
                    testTag = "password_form_new",
                )

                Spacer(modifier = Modifier.height(12.dp))

                PasswordField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        error = null
                    },
                    label = "Konfirmasi kata sandi baru",
                    testTag = "password_form_confirm",
                    imeAction = ImeAction.Done,
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Kata sandi disimpan sebagai hash SHA-256 + salt, bukan teks biasa.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                DialogError(error)

                Spacer(modifier = Modifier.height(18.dp))
                DialogButtons(
                    confirmLabel = "Simpan",
                    confirmTestTag = "password_form_submit",
                    onDismiss = onDismiss,
                    onConfirm = { submit() },
                )
            }
        }
    }
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    testTag: String,
    imeAction: ImeAction = ImeAction.Next,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        label = { Text(label) },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction,
        ),
    )
}

@Composable
private fun DialogHeader(
    title: String,
    subtitle: String,
    onDismiss: () -> Unit,
    closeTestTag: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag(closeTestTag),
        ) {
            Icon(Icons.Rounded.Close, contentDescription = "Tutup")
        }
    }
}

@Composable
private fun DialogError(error: String?) {
    if (error == null) return
    Spacer(modifier = Modifier.height(12.dp))
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.errorContainer,
    ) {
        Text(
            text = error,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(12.dp),
        )
    }
}

@Composable
private fun DialogButtons(
    confirmLabel: String,
    confirmTestTag: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(onClick = onDismiss) {
            Text("Batal")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = onConfirm,
            modifier = Modifier.testTag(confirmTestTag),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(confirmLabel)
        }
    }
}
