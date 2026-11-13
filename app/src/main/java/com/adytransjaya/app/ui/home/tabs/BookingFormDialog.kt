package com.adytransjaya.app.ui.home.tabs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.adytransjaya.app.data.RentalDates
import com.adytransjaya.app.data.model.Booking
import com.adytransjaya.app.data.model.BookingStatus
import com.adytransjaya.app.data.model.Car
import com.adytransjaya.app.data.model.CarStatus
import com.adytransjaya.app.ui.common.formatRupiah

/**
 * Dialog pembuatan pesanan untuk pelanggan.
 *
 * Validasi tanggal dan bentrok jadwal dilakukan di sini agar pengguna
 * mendapat umpan balik cepat; repository tetap memvalidasi ulang.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingFormDialog(
    cars: List<Car>,
    bookings: List<Booking>,
    onDismiss: () -> Unit,
    onSubmit: (carId: String, startEpochDay: Long, endEpochDay: Long) -> Unit,
) {
    val today = remember { RentalDates.todayEpochDay() }
    val availableCars = remember(cars) {
        cars.filter { it.status == CarStatus.AVAILABLE }
    }

    var selectedCarId by remember { mutableStateOf(availableCars.firstOrNull()?.id) }
    var startDay by remember { mutableStateOf<Long?>(null) }
    var endDay by remember { mutableStateOf<Long?>(null) }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val selectedCar = availableCars.firstOrNull { it.id == selectedCarId }
    val days = if (startDay != null && endDay != null) (endDay!! - startDay!!) else 0L
    val total = if (selectedCar != null && days > 0) days * selectedCar.pricePerDay else 0L

    fun submit() {
        val car = selectedCar
        val start = startDay
        val end = endDay
        val validationError = when {
            car == null -> "Pilih mobil yang ingin disewa."
            start == null || end == null -> "Pilih tanggal mulai dan tanggal selesai."
            start < today -> "Tanggal mulai tidak boleh di masa lalu."
            end <= start -> "Tanggal selesai harus setelah tanggal mulai."
            bookings.any { booking ->
                booking.carId == car.id &&
                    (booking.status == BookingStatus.PENDING ||
                        booking.status == BookingStatus.ACTIVE) &&
                    hasOverlap(booking, start, end)
            } -> "Mobil sudah dipesan pada rentang tanggal tersebut."
            else -> null
        }

        if (validationError != null) {
            error = validationError
            return
        }

        onSubmit(car!!.id, start!!, end!!)
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Buat pesanan",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Pilih mobil dan tentukan tanggal sewa.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("booking_form_close"),
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (availableCars.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                    ) {
                        Text(
                            text = "Belum ada mobil yang tersedia. Coba lagi nanti.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp),
                        )
                    }
                } else {
                    Text(
                        text = "Pilih mobil",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        availableCars.forEach { car ->
                            val selected = car.id == selectedCarId
                            Surface(
                                onClick = {
                                    selectedCarId = car.id
                                    error = null
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("booking_form_car_${car.id}"),
                                shape = RoundedCornerShape(14.dp),
                                color = if (selected) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surface
                                },
                                border = BorderStroke(
                                    1.dp,
                                    if (selected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.outlineVariant
                                    },
                                ),
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    RadioButton(
                                        selected = selected,
                                        onClick = null,
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = car.name,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                        Text(
                                            text = "${car.plate} · ${car.seats} kursi · " +
                                                car.transmission,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                    Text(
                                        text = "${formatRupiah(car.pricePerDay)}/hari",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Tanggal sewa",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                DateField(
                    label = "Mulai",
                    epochDay = startDay,
                    testTag = "booking_start_date",
                    onClick = { showStartPicker = true },
                )
                Spacer(modifier = Modifier.height(8.dp))
                DateField(
                    label = "Selesai",
                    epochDay = endDay,
                    testTag = "booking_end_date",
                    onClick = { showEndPicker = true },
                )

                if (selectedCar != null && startDay != null && endDay != null && days > 0) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "$days hari × ${formatRupiah(selectedCar.pricePerDay)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.weight(1f),
                                )
                                Text(
                                    text = formatRupiah(total),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Pesanan akan berstatus Menunggu sampai disetujui.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                        }
                    }
                }

                if (error != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("booking_form_error"),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                    ) {
                        Text(
                            text = error.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(12.dp))

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
                        onClick = { submit() },
                        modifier = Modifier.testTag("booking_form_submit"),
                        enabled = availableCars.isNotEmpty(),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text("Buat pesanan")
                    }
                }
            }
        }
    }

    if (showStartPicker) {
        DayPickerDialog(
            title = "Tanggal mulai",
            initialEpochDay = startDay ?: today,
            onDismiss = { showStartPicker = false },
            onConfirm = {
                startDay = it
                if (endDay != null && endDay!! <= it) endDay = null
                error = null
                showStartPicker = false
            },
        )
    }

    if (showEndPicker) {
        DayPickerDialog(
            title = "Tanggal selesai",
            initialEpochDay = endDay ?: startDay?.plus(1) ?: today.plus(1),
            onDismiss = { showEndPicker = false },
            onConfirm = {
                endDay = it
                error = null
                showEndPicker = false
            },
        )
    }
}

@Composable
private fun DateField(
    label: String,
    epochDay: Long?,
    testTag: String,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = 14.dp,
            vertical = 12.dp,
        ),
    ) {
        Icon(
            imageVector = Icons.Rounded.CalendarMonth,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (epochDay == null) {
                "Pilih tanggal $label"
            } else {
                RentalDates.formatEpochDay(epochDay)
            },
            modifier = Modifier.weight(1f),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DayPickerDialog(
    title: String,
    initialEpochDay: Long,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit,
) {
    val state = rememberDatePickerState(
        initialSelectedDateMillis = RentalDates.millisFromEpochDay(initialEpochDay),
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val millis = state.selectedDateMillis
                    if (millis != null) {
                        onConfirm(millis / RentalDates.DAY_MILLIS)
                    }
                },
                modifier = Modifier.testTag("date_picker_confirm"),
            ) {
                Text("Pilih")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        },
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 24.dp, top = 20.dp),
            )
            DatePicker(state = state, showModeToggle = false)
        }
    }
}

private fun hasOverlap(booking: Booking, startEpochDay: Long, endEpochDay: Long): Boolean {
    val existingStart = RentalDates.parseEpochDay(booking.startDate) ?: return false
    val existingEnd = RentalDates.parseEpochDay(booking.endDate) ?: return false
    return startEpochDay < existingEnd && endEpochDay > existingStart
}
