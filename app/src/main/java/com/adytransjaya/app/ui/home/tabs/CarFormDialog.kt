package com.adytransjaya.app.ui.home.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.adytransjaya.app.data.model.Car
import com.adytransjaya.app.data.model.CarStatus
import java.util.Calendar
import java.util.UUID

/**
 * Dialog untuk menambah atau mengedit mobil.
 *
 * @param car null berarti mode tambah; selain itu mode edit.
 */
@Composable
fun CarFormDialog(
    existingCars: List<Car>,
    car: Car?,
    onDismiss: () -> Unit,
    onSave: (Car, Boolean) -> Unit,
) {
    val currentYear = remember { Calendar.getInstance().get(Calendar.YEAR) }
    val isNew = car == null

    var name by remember(car?.id) { mutableStateOf(car?.name.orEmpty()) }
    var brand by remember(car?.id) { mutableStateOf(car?.brand.orEmpty()) }
    var plate by remember(car?.id) { mutableStateOf(car?.plate.orEmpty()) }
    var seats by remember(car?.id) { mutableStateOf(car?.seats?.toString() ?: "7") }
    var pricePerDay by remember(car?.id) {
        mutableStateOf(car?.pricePerDay?.toString().orEmpty())
    }
    var year by remember(car?.id) {
        mutableStateOf(car?.year?.toString() ?: currentYear.toString())
    }
    var transmission by remember(car?.id) {
        mutableStateOf(car?.transmission ?: "Matic")
    }
    var status by remember(car?.id) {
        mutableStateOf(car?.status ?: CarStatus.AVAILABLE)
    }
    var error by remember(car?.id) { mutableStateOf<String?>(null) }

    fun submit() {
        val normalizedPlate = plate.trim().uppercase()
        val seatsValue = seats.toIntOrNull()
        val priceValue = pricePerDay.toLongOrNull()
        val yearValue = year.toIntOrNull()

        val validationError = when {
            name.trim().length < 2 -> "Nama mobil minimal 2 karakter."
            brand.trim().isBlank() -> "Merek mobil wajib diisi."
            normalizedPlate.length < 3 -> "Plat nomor wajib diisi."
            existingCars.any {
                it.id != car?.id && it.plate.equals(normalizedPlate, ignoreCase = true)
            } -> "Plat nomor $normalizedPlate sudah terdaftar."
            seatsValue == null || seatsValue !in 1..50 ->
                "Jumlah kursi harus antara 1 dan 50."
            priceValue == null || priceValue <= 0 ->
                "Harga per hari harus lebih dari 0."
            yearValue == null || yearValue !in 1990..(currentYear + 1) ->
                "Tahun harus antara 1990 dan ${currentYear + 1}."
            else -> null
        }

        if (validationError != null) {
            error = validationError
            return
        }

        val result = Car(
            id = car?.id ?: "car-${UUID.randomUUID()}",
            name = name.trim(),
            brand = brand.trim(),
            plate = normalizedPlate,
            seats = seatsValue!!,
            transmission = transmission,
            pricePerDay = priceValue!!,
            status = status,
            year = yearValue!!,
        )
        onSave(result, isNew)
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
                            text = if (isNew) "Tambah mobil" else "Edit mobil",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = if (isNew) {
                                "Lengkapi data unit baru."
                            } else {
                                "Perbarui data ${car?.name.orEmpty()}."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("car_form_close"),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Tutup",
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        error = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("car_form_name"),
                    label = { Text("Nama mobil") },
                    placeholder = { Text("Contoh: Toyota Avanza") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = brand,
                    onValueChange = {
                        brand = it
                        error = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("car_form_brand"),
                    label = { Text("Merek") },
                    placeholder = { Text("Contoh: Toyota") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = plate,
                    onValueChange = {
                        plate = it.uppercase()
                        error = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("car_form_plate"),
                    label = { Text("Plat nomor") },
                    placeholder = { Text("B 1234 ADY") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = seats,
                        onValueChange = { value ->
                            seats = value.filter(Char::isDigit).take(2)
                            error = null
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("car_form_seats"),
                        label = { Text("Kursi") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next,
                        ),
                    )
                    OutlinedTextField(
                        value = year,
                        onValueChange = { value ->
                            year = value.filter(Char::isDigit).take(4)
                            error = null
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("car_form_year"),
                        label = { Text("Tahun") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next,
                        ),
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = pricePerDay,
                    onValueChange = { value ->
                        pricePerDay = value.filter(Char::isDigit).take(9)
                        error = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("car_form_price"),
                    label = { Text("Harga per hari (Rp)") },
                    placeholder = { Text("350000") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Transmisi",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Matic", "Manual").forEach { option ->
                        FilterChip(
                            selected = transmission == option,
                            onClick = {
                                transmission = option
                                error = null
                            },
                            label = { Text(option) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("car_form_transmission_${option.lowercase()}"),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Status",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    CarStatus.entries.forEach { option ->
                        FilterChip(
                            selected = status == option,
                            onClick = {
                                status = option
                                error = null
                            },
                            label = { Text(option.label) },
                            modifier = Modifier.testTag(
                                "car_form_status_${option.name.lowercase()}",
                            ),
                        )
                    }
                }

                if (error != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("car_form_error"),
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

                Spacer(modifier = Modifier.height(18.dp))

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
                        modifier = Modifier.testTag("car_form_submit"),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(if (isNew) "Tambah" else "Simpan")
                    }
                }
            }
        }
    }
}
