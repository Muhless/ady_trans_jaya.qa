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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adytransjaya.app.data.DemoData
import com.adytransjaya.app.data.model.Booking
import com.adytransjaya.app.data.model.BookingStatus
import com.adytransjaya.app.data.model.Car
import com.adytransjaya.app.data.model.PaymentStatus
import com.adytransjaya.app.data.model.Role
import com.adytransjaya.app.data.model.User
import com.adytransjaya.app.ui.common.BookingStatusPill
import com.adytransjaya.app.ui.common.EmptyState
import com.adytransjaya.app.ui.common.formatRupiah

private data class BookingFilter(
    val key: String,
    val label: String,
    val status: BookingStatus?,
)

private val bookingFilters = listOf(
    BookingFilter("ALL", "Semua", null),
    BookingFilter("ACTIVE", "Aktif", BookingStatus.ACTIVE),
    BookingFilter("PENDING", "Menunggu", BookingStatus.PENDING),
    BookingFilter("COMPLETED", "Selesai", BookingStatus.COMPLETED),
    BookingFilter("CANCELLED", "Dibatalkan", BookingStatus.CANCELLED),
)

@Composable
fun BookingsTab(
    user: User,
    bookings: List<Booking>,
    cars: List<Car>,
    customers: List<User>,
    onCreateBooking: (carId: String, startEpochDay: Long, endEpochDay: Long) -> Unit,
    onUpdateStatus: (code: String, newStatus: BookingStatus) -> Unit,
    onUpdatePayment: (code: String, newStatus: PaymentStatus) -> Unit,
) {
    var selectedFilterKey by rememberSaveable { mutableStateOf("ALL") }
    var showBookingForm by remember { mutableStateOf(false) }
    var detailBookingCode by rememberSaveable { mutableStateOf<String?>(null) }
    val allBookings = remember(user.id, user.role, bookings) {
        if (user.role == Role.CUSTOMER) {
            bookings.filter { it.customerId == user.id }
        } else {
            bookings
        }
    }
    val filtered = remember(selectedFilterKey, allBookings) {
        val filter = bookingFilters.firstOrNull { it.key == selectedFilterKey }?.status
        if (filter == null) {
            allBookings
        } else {
            allBookings.filter { it.status == filter }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = if (user.role == Role.CUSTOMER) "Pesanan saya" else "Daftar pesanan",
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${allBookings.size} pesanan ditemukan",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (user.role == Role.CUSTOMER) {
            item {
                Button(
                    onClick = { showBookingForm = true },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("add_booking"),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DirectionsCar,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Buat pesanan")
                }
            }
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(bookingFilters, key = { it.key }) { filter ->
                    FilterChip(
                        selected = selectedFilterKey == filter.key,
                        onClick = { selectedFilterKey = filter.key },
                        label = { Text(filter.label) },
                    )
                }
            }
        }

        if (filtered.isEmpty()) {
            item {
                EmptyState(
                    title = "Tidak ada pesanan",
                    message = if (user.role == Role.CUSTOMER) {
                        "Tekan \"Buat pesanan\" untuk mulai menyewa mobil."
                    } else {
                        "Belum ada pesanan pada filter ini."
                    },
                    icon = Icons.Rounded.ReceiptLong,
                )
            }
        } else {
            items(filtered, key = { it.code }) { booking ->
                BookingCard(
                    booking = booking,
                    car = cars.firstOrNull { it.id == booking.carId },
                    customer = customers.firstOrNull { it.id == booking.customerId }
                        ?: DemoData.customerById(booking.customerId),
                    userRole = user.role,
                    onUpdateStatus = { status -> onUpdateStatus(booking.code, status) },
                    onOpenDetail = { detailBookingCode = booking.code },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    if (showBookingForm) {
        BookingFormDialog(
            cars = cars,
            bookings = bookings,
            onDismiss = { showBookingForm = false },
            onSubmit = { carId, startDay, endDay ->
                onCreateBooking(carId, startDay, endDay)
                showBookingForm = false
            },
        )
    }

    val detailBooking = detailBookingCode?.let { code ->
        allBookings.firstOrNull { it.code == code }
    }
    if (detailBooking != null) {
        BookingDetailDialog(
            booking = detailBooking,
            car = cars.firstOrNull { it.id == detailBooking.carId },
            customer = customers.firstOrNull { it.id == detailBooking.customerId }
                ?: DemoData.customerById(detailBooking.customerId),
            userRole = user.role,
            onUpdatePayment = { status -> onUpdatePayment(detailBooking.code, status) },
            onDismiss = { detailBookingCode = null },
        )
    }
}

@Composable
private fun BookingCard(
    booking: Booking,
    car: Car?,
    customer: User?,
    userRole: Role,
    onUpdateStatus: (BookingStatus) -> Unit,
    onOpenDetail: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onOpenDetail,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column {
            Row(
                modifier = Modifier.padding(start = 14.dp, end = 14.dp, top = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(13.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DirectionsCar,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(23.dp),
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
                        text = car?.plate ?: booking.code,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                BookingStatusPill(status = booking.status)
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
            )

            Column(modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 14.dp)) {
                if (userRole != Role.CUSTOMER && customer != null) {
                    DetailLine(label = "Pelanggan", value = customer.name)
                }
                DetailLine(label = "Kode", value = booking.code)
                DetailLine(
                    label = "Tanggal",
                    value = "${booking.startDate} – ${booking.endDate} (${booking.days} hari)",
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                    )
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = formatRupiah(booking.total),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = booking.paymentStatus.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = when (booking.paymentStatus) {
                                PaymentStatus.PAID -> MaterialTheme.colorScheme.primary
                                PaymentStatus.UNPAID -> MaterialTheme.colorScheme.error
                                PaymentStatus.DOWN_PAYMENT ->
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                    }
                }
            }

            val canApprove =
                userRole != Role.CUSTOMER && booking.status == BookingStatus.PENDING
            val canComplete =
                userRole != Role.CUSTOMER && booking.status == BookingStatus.ACTIVE
            val canCancel = when (userRole) {
                Role.CUSTOMER -> booking.status == BookingStatus.PENDING
                else -> booking.status == BookingStatus.PENDING ||
                    booking.status == BookingStatus.ACTIVE
            }

            if (canApprove || canComplete || canCancel) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (canCancel) {
                        TextButton(
                            onClick = { onUpdateStatus(BookingStatus.CANCELLED) },
                            modifier = Modifier.testTag("booking_cancel_${booking.code}"),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (userRole == Role.CUSTOMER) {
                                    "Batalkan pesanan"
                                } else {
                                    "Batalkan"
                                },
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                    if (canApprove) {
                        TextButton(
                            onClick = { onUpdateStatus(BookingStatus.ACTIVE) },
                            modifier = Modifier.testTag("booking_approve_${booking.code}"),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Setujui")
                        }
                    }
                    if (canComplete) {
                        TextButton(
                            onClick = { onUpdateStatus(BookingStatus.COMPLETED) },
                            modifier = Modifier.testTag("booking_complete_${booking.code}"),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DoneAll,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Selesaikan")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(82.dp),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f),
        )
    }
}
