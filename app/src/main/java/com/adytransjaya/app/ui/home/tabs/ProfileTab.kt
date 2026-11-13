package com.adytransjaya.app.ui.home.tabs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.adytransjaya.app.data.model.Role
import com.adytransjaya.app.data.model.User
import com.adytransjaya.app.ui.common.Avatar
import com.adytransjaya.app.ui.common.InfoRow
import com.adytransjaya.app.ui.common.RoleBadge
import com.adytransjaya.app.ui.common.SectionHeader
import com.adytransjaya.app.ui.theme.BrandPrimary
import com.adytransjaya.app.ui.theme.BrandTeal

@Composable
fun ProfileTab(
    user: User,
    employees: List<User>,
    onSaveProfile: (name: String, phone: String) -> Unit,
    onChangePassword: (currentPassword: String, newPassword: String) -> Unit,
    onCreateEmployee: (name: String, email: String, phone: String, password: String) -> Unit,
    onToggleEmployeeActive: (userId: String, active: Boolean) -> Unit,
    onLogoutRequest: () -> Unit,
) {
    var showEditProfile by remember { mutableStateOf(false) }
    var showChangePassword by remember { mutableStateOf(false) }
    var showEmployeeManagement by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ProfileHeader(user = user)
        }

        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionHeader(title = "Informasi akun")
                    Spacer(modifier = Modifier.height(6.dp))
                    InfoRow(label = "Nama", value = user.name)
                    InfoRow(label = "Email", value = user.email)
                    InfoRow(label = "Telepon", value = user.phone)
                    InfoRow(label = "Peran", value = user.role.label)
                    InfoRow(label = "Bergabung", value = user.joinedAt)
                }
            }
        }

        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionHeader(title = "Pengaturan akun")
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { showEditProfile = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("profile_edit"),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Edit profil")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { showChangePassword = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("profile_change_password"),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Password,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ganti kata sandi")
                    }
                    if (user.role == Role.OWNER) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { showEmployeeManagement = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("employee_manage"),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Badge,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Kelola akun karyawan")
                        }
                    }
                }
            }
        }

        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    RoundedCornerShape(12.dp),
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Shield,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Hak akses ${user.role.label}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = roleAccessDescription(user.role),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    RoleBadge(role = user.role)
                }
            }
        }

        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tentang aplikasi",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    InfoRow(label = "Aplikasi", value = "Ady Trans Jaya")
                    InfoRow(label = "Versi", value = "1.0.0")
                    InfoRow(label = "Paket", value = "com.adytransjaya.app")
                }
            }
        }

        item {
            OutlinedButton(
                onClick = onLogoutRequest,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("logout_button"),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.45f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                ),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Keluar dari akun")
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(13.dp),
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "Sesi login tersimpan dengan aman di perangkat ini",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }

        item {
            Text(
                text = "© 2026 Ady Trans Jaya",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    if (showEditProfile) {
        EditProfileDialog(
            user = user,
            onDismiss = { showEditProfile = false },
            onSave = { name, phone ->
                onSaveProfile(name, phone)
                showEditProfile = false
            },
        )
    }

    if (showChangePassword) {
        ChangePasswordDialog(
            onDismiss = { showChangePassword = false },
            onSave = { current, new ->
                onChangePassword(current, new)
                showChangePassword = false
            },
        )
    }

    if (showEmployeeManagement && user.role == Role.OWNER) {
        EmployeeManagementDialog(
            employees = employees,
            onDismiss = { showEmployeeManagement = false },
            onCreateEmployee = onCreateEmployee,
            onToggleActive = onToggleEmployeeActive,
        )
    }
}

@Composable
private fun ProfileHeader(user: User) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        BrandPrimary,
                        BrandTeal,
                    ),
                ),
            )
            .padding(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(50),
                color = Color.White.copy(alpha = 0.18f),
            ) {
                Box(modifier = Modifier.padding(4.dp)) {
                    Avatar(user = user, size = 64.dp)
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.82f),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.16f),
                ) {
                    Text(
                        text = user.role.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                }
            }
        }
    }
}

private fun roleAccessDescription(role: Role): String = when (role) {
    Role.OWNER ->
        "Anda memiliki akses penuh untuk memantau laporan, mengelola mobil, " +
            "memproses pesanan, dan melihat seluruh data pelanggan."

    Role.EMPLOYEE ->
        "Anda dapat mengelola ketersediaan mobil, memproses pesanan pelanggan, " +
            "dan melihat data pelanggan. Laporan keuangan hanya dapat diakses pemilik."

    Role.CUSTOMER ->
        "Anda dapat melihat katalog mobil, memantau pesanan yang sedang berjalan, " +
            "dan melihat riwayat perjalanan Anda."
}
