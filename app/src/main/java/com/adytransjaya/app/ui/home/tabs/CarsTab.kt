package com.adytransjaya.app.ui.home.tabs

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.adytransjaya.app.data.model.Car
import com.adytransjaya.app.data.model.CarStatus
import com.adytransjaya.app.data.model.Role
import com.adytransjaya.app.data.model.User
import com.adytransjaya.app.ui.common.CarIconBox
import com.adytransjaya.app.ui.common.CarStatusPill
import com.adytransjaya.app.ui.common.EmptyState
import com.adytransjaya.app.ui.common.SpecChip
import com.adytransjaya.app.ui.common.formatCompactRupiah
import com.adytransjaya.app.ui.theme.Success
import com.adytransjaya.app.ui.theme.SuccessLight

@Composable
fun CarsTab(
    user: User,
    cars: List<Car>,
    onSaveCar: (Car, Boolean) -> Unit,
    onDeleteCar: (String) -> Unit,
) {
    val canManage = user.role != Role.CUSTOMER
    var query by rememberSaveable { mutableStateOf("") }
    var formCar by remember { mutableStateOf<Car?>(null) }
    var showForm by remember { mutableStateOf(false) }
    var carToDelete by remember { mutableStateOf<Car?>(null) }
    var detailCarId by rememberSaveable { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()

    // Dibaca ulang dari daftar terbaru agar rincian ikut berubah setelah edit
    // dan otomatis kembali ke daftar setelah mobil dihapus.
    val detailCar = detailCarId?.let { id -> cars.firstOrNull { it.id == id } }

    BackHandler(enabled = detailCar != null) { detailCarId = null }

    val filteredCars = remember(query, cars) {
        if (query.isBlank()) {
            cars
        } else {
            cars.filter {
                it.name.contains(query, ignoreCase = true) ||
                    it.brand.contains(query, ignoreCase = true) ||
                    it.plate.contains(query, ignoreCase = true)
            }
        }
    }

    val availableCount = cars.count { it.status == CarStatus.AVAILABLE }

    if (detailCar != null) {
        CarDetailContent(
            car = detailCar,
            canManage = canManage,
            onBack = { detailCarId = null },
            onEdit = {
                formCar = detailCar
                showForm = true
            },
            onDelete = { carToDelete = detailCar },
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Column {
                    Text(
                        text = if (user.role == Role.CUSTOMER) "Katalog mobil" else "Kelola mobil",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${cars.size} unit terdaftar · $availableCount tersedia",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (canManage) {
                item {
                    Button(
                        onClick = {
                            formCar = null
                            showForm = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("add_car"),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tambah mobil")
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("car_search"),
                    placeholder = { Text("Cari merek, nama, atau plat nomor") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                )
            }

            if (filteredCars.isEmpty()) {
                item {
                    EmptyState(
                        title = "Mobil tidak ditemukan",
                        message = "Coba kata kunci lain atau kosongkan pencarian.",
                        icon = Icons.Rounded.Search,
                    )
                }
            } else {
                items(filteredCars, key = { it.id }) { car ->
                    CarCard(
                        car = car,
                        onClick = { detailCarId = car.id },
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    if (showForm) {
        CarFormDialog(
            existingCars = cars,
            car = formCar,
            onDismiss = {
                showForm = false
                formCar = null
            },
            onSave = { car, isNew ->
                onSaveCar(car, isNew)
                showForm = false
                formCar = null
            },
        )
    }

    carToDelete?.let { car ->
        AlertDialog(
            onDismissRequest = { carToDelete = null },
            title = { Text("Hapus mobil?") },
            text = {
                Text(
                    "Mobil ${car.name} (${car.plate}) akan dihapus dari daftar. " +
                        "Tindakan ini tidak bisa dibatalkan.",
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteCar(car.id)
                        carToDelete = null
                    },
                    modifier = Modifier.testTag("car_delete_confirm"),
                ) {
                    Text(
                        text = "Hapus",
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { carToDelete = null }) {
                    Text("Batal")
                }
            },
        )
    }
}

@Composable
private fun CarCard(
    car: Car,
    onClick: () -> Unit,
) {
    val (container, content) = when (car.status) {
        CarStatus.AVAILABLE -> SuccessLight to Success
        CarStatus.RENTED -> MaterialTheme.colorScheme.primaryContainer to
            MaterialTheme.colorScheme.onPrimaryContainer
        CarStatus.MAINTENANCE -> MaterialTheme.colorScheme.tertiaryContainer to
            MaterialTheme.colorScheme.onTertiaryContainer
    }

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("car_card_${car.id}"),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CarIconBox(
                containerColor = container,
                contentColor = content,
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.Top) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = car.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = "${car.brand} · ${car.year}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    CarStatusPill(status = car.status)
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = car.plate,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SpecChip(text = "${car.seats} kursi")
                    SpecChip(text = car.transmission)
                    SpecChip(text = "${formatCompactRupiah(car.pricePerDay)}/hari")
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}
