package com.adytransjaya.app.ui.home.tabs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.adytransjaya.app.data.model.User

/**
 * Dialog khusus Pemilik untuk membuat akun karyawan dan menonaktifkannya.
 */
@Composable
fun EmployeeManagementDialog(
    employees: List<User>,
    onDismiss: () -> Unit,
    onCreateEmployee: (name: String, email: String, phone: String, password: String) -> Unit,
    onToggleActive: (userId: String, active: Boolean) -> Unit,
) {
    var showForm by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 680.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 18.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Kelola akun karyawan",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Akun karyawan dibuat dan diatur oleh Pemilik.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("employee_manage_close"),
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (showForm) {
                    EmployeeForm(
                        onCancel = { showForm = false },
                        onSubmit = { name, email, phone, password ->
                            onCreateEmployee(name, email, phone, password)
                            showForm = false
                        },
                    )
                } else {
                    OutlinedButton(
                        onClick = { showForm = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("employee_add"),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PersonAdd,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tambah karyawan")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "${employees.size} akun karyawan",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (employees.isEmpty()) {
                    Text(
                        text = "Belum ada akun karyawan. Tambahkan akun pertama.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 18.dp),
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        employees.forEach { employee ->
                            EmployeeRow(
                                employee = employee,
                                onToggleActive = { active ->
                                    onToggleActive(employee.id, active)
                                },
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Karyawan yang dinonaktifkan tidak bisa masuk, " +
                        "tetapi riwayat pesanan dan datanya tetap tersimpan.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Tutup")
                    }
                }
            }
        }
    }
}

@Composable
private fun EmployeeRow(
    employee: User,
    onToggleActive: (Boolean) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(12.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = employee.initials,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = employee.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = employee.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
                Text(
                    text = if (employee.isActive) "Aktif" else "Nonaktif",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (employee.isActive) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                )
            }
            Switch(
                checked = employee.isActive,
                onCheckedChange = onToggleActive,
                modifier = Modifier.testTag("employee_toggle_${employee.id}"),
            )
        }
    }
}

@Composable
private fun EmployeeForm(
    onCancel: () -> Unit,
    onSubmit: (name: String, email: String, phone: String, password: String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    fun submit() {
        val validationError = when {
            name.trim().length < 3 -> "Nama minimal 3 karakter."
            !email.contains('@') || !email.contains('.') -> "Format email tidak valid."
            phone.filter(Char::isDigit).length !in 10..15 ->
                "Nomor telepon harus terdiri dari 10–15 digit."

            password.length < 6 -> "Kata sandi minimal 6 karakter."
            else -> null
        }
        if (validationError != null) {
            error = validationError
            return
        }
        onSubmit(name.trim(), email.trim(), phone.trim(), password)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Badge,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Akun karyawan baru",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    error = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("employee_form_name"),
                label = { Text("Nama lengkap") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    error = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("employee_form_email"),
                label = { Text("Email") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = {
                    phone = it
                    error = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("employee_form_phone"),
                label = { Text("Nomor telepon") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next,
                ),
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    error = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("employee_form_password"),
                label = { Text("Kata sandi awal") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
            )

            if (error != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("employee_form_error"),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                ) {
                    Text(
                        text = error.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(10.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = onCancel) {
                    Text("Batal")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { submit() },
                    modifier = Modifier.testTag("employee_form_submit"),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("Tambah akun")
                }
            }
        }
    }
}
