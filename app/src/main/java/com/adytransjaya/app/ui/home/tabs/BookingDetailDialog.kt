package com.adytransjaya.app.ui.home.tabs

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.adytransjaya.app.data.RentalDates
import com.adytransjaya.app.data.model.Booking
import com.adytransjaya.app.data.model.BookingStatus
import com.adytransjaya.app.data.model.Car
import com.adytransjaya.app.data.model.PaymentStatus
import com.adytransjaya.app.data.model.Role
import com.adytransjaya.app.data.model.User
import com.adytransjaya.app.ui.common.BookingStatusPill
import com.adytransjaya.app.ui.common.formatRupiah

/**
 * Rincian pesanan yang berfungsi sebagai invoice sederhana.
 * Staf bisa langsung menghubungi pelanggan lewat aplikasi telepon.
 */
@Composable
fun BookingDetailDialog(
    booking: Booking,
    car: Car?,
    customer: User?,
    userRole: Role,
    onUpdatePayment: (PaymentStatus) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val customerPhoneDigits = customer?.phone?.filter(Char::isDigit).orEmpty()
    val canContactCustomer =
        userRole != Role.CUSTOMER && customerPhoneDigits.length >= 8
    val canManagePayment =
        userRole != Role.CUSTOMER && booking.status != BookingStatus.CANCELLED
    val today = RentalDates.todayEpochDay()
    val endEpochDay = RentalDates.parseEpochDay(booking.endDate)
    val lateDays = if (
        booking.status == BookingStatus.ACTIVE &&
        endEpochDay != null &&
        endEpochDay < today
    ) {
        (today - endEpochDay).toInt()
    } else {
        0
    }
    val pricePerDay = if (booking.days > 0) booking.total / booking.days else booking.total
    val estimatedFine = lateDays.toLong() * (pricePerDay / 10)

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Detail pesanan",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = booking.code,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("booking_detail_close"),
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                RoundedCornerShape(14.dp),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DirectionsCar,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = car?.name ?: "Mobil dihapus",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = car?.plate ?: "-",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    BookingStatusPill(status = booking.status)
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(14.dp))

                InfoSection(title = "Pelanggan", icon = Icons.Rounded.Person) {
                    DetailRow(
                        label = if (userRole == Role.CUSTOMER) "Nama" else "Nama pelanggan",
                        value = customer?.name ?: "Tidak diketahui",
                    )
                    if (userRole != Role.CUSTOMER) {
                        DetailRow(
                            label = "Telepon",
                            value = customer?.phone ?: "-",
                        )
                        DetailRow(
                            label = "Email",
                            value = customer?.email ?: "-",
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                InfoSection(title = "Jadwal sewa", icon = Icons.Rounded.CalendarMonth) {
                    DetailRow(label = "Mulai", value = booking.startDate)
                    DetailRow(label = "Selesai", value = booking.endDate)
                    DetailRow(label = "Durasi", value = "${booking.days} hari")
                }

                Spacer(modifier = Modifier.height(12.dp))

                InfoSection(title = "Rincian biaya", icon = Icons.Rounded.ReceiptLong) {
                    DetailRow(
                        label = "Harga per hari",
                        value = formatRupiah(pricePerDay),
                    )
                    DetailRow(
                        label = "${booking.days} hari × ${formatRupiah(pricePerDay)}",
                        value = formatRupiah(booking.total),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = formatRupiah(booking.total),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                InfoSection(title = "Pembayaran", icon = Icons.Rounded.Payments) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Status",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f),
                        )
                        PaymentStatusPill(status = booking.paymentStatus)
                    }
                    DetailRow(
                        label = "Sudah dibayar",
                        value = formatRupiah(booking.paidAmount),
                    )
                    DetailRow(
                        label = "Sisa tagihan",
                        value = formatRupiah(
                            (booking.total - booking.paidAmount).coerceAtLeast(0L),
                        ),
                    )
                    if (canManagePayment && booking.paymentStatus != PaymentStatus.PAID) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (booking.paymentStatus == PaymentStatus.UNPAID) {
                                OutlinedButton(
                                    onClick = { onUpdatePayment(PaymentStatus.DOWN_PAYMENT) },
                                    modifier = Modifier.testTag("booking_payment_dp"),
                                    shape = RoundedCornerShape(10.dp),
                                ) {
                                    Text("Catat DP 50%")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Button(
                                onClick = { onUpdatePayment(PaymentStatus.PAID) },
                                modifier = Modifier.testTag("booking_payment_paid"),
                                shape = RoundedCornerShape(10.dp),
                            ) {
                                Text("Tandai lunas")
                            }
                        }
                    }
                }

                if (lateDays > 0) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.WarningAmber,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Terlambat $lateDays hari",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                )
                                Text(
                                    text = "Estimasi denda " +
                                        "${formatRupiah(estimatedFine)} (10% harga harian " +
                                        "per hari keterlambatan).",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                ) {
                    Text(
                        text = statusDescription(booking.status),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(12.dp),
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Tutup")
                    }
                    if (canContactCustomer) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val intent = Intent(
                                    Intent.ACTION_DIAL,
                                    Uri.parse("tel:$customerPhoneDigits"),
                                )
                                context.startActivity(intent)
                            },
                            modifier = Modifier.testTag("booking_detail_contact"),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Phone,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Hubungi")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(17.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun PaymentStatusPill(status: PaymentStatus) {
    val (container, content) = when (status) {
        PaymentStatus.UNPAID ->
            MaterialTheme.colorScheme.errorContainer to
                MaterialTheme.colorScheme.onErrorContainer

        PaymentStatus.DOWN_PAYMENT ->
            MaterialTheme.colorScheme.secondaryContainer to
                MaterialTheme.colorScheme.onSecondaryContainer

        PaymentStatus.PAID ->
            MaterialTheme.colorScheme.primaryContainer to
                MaterialTheme.colorScheme.onPrimaryContainer
    }

    Surface(shape = RoundedCornerShape(50), color = container) {
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = content,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
        )
    }
}

private fun statusDescription(status: BookingStatus): String = when (status) {
    BookingStatus.PENDING ->
        "Pesanan menunggu konfirmasi. Mobil baru disewakan setelah disetujui."

    BookingStatus.ACTIVE ->
        "Pesanan sedang berjalan. Mobil berstatus Disewakan sampai dikembalikan."

    BookingStatus.COMPLETED ->
        "Pesanan selesai. Mobil sudah kembali Tersedia dan transaksi masuk laporan."

    BookingStatus.CANCELLED ->
        "Pesanan dibatalkan. Tidak ada biaya yang ditagihkan untuk pesanan ini."
}
