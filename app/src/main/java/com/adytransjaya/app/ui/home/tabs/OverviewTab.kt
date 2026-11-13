package com.adytransjaya.app.ui.home.tabs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ConfirmationNumber
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.EventAvailable
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.TaskAlt
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adytransjaya.app.data.DemoData
import com.adytransjaya.app.data.DemoUsers
import com.adytransjaya.app.data.model.Booking
import com.adytransjaya.app.data.model.BookingStatus
import com.adytransjaya.app.data.model.Car
import com.adytransjaya.app.data.model.Role
import com.adytransjaya.app.data.model.User
import com.adytransjaya.app.ui.common.BookingStatusPill
import com.adytransjaya.app.ui.common.EmptyState
import com.adytransjaya.app.ui.common.RoleBadge
import com.adytransjaya.app.ui.common.SectionHeader
import com.adytransjaya.app.ui.common.formatCompactRupiah
import com.adytransjaya.app.ui.common.formatRupiah
import com.adytransjaya.app.ui.home.HomeTab
import com.adytransjaya.app.ui.theme.BrandPrimary
import com.adytransjaya.app.ui.theme.BrandTeal
import com.adytransjaya.app.ui.theme.Success
import com.adytransjaya.app.ui.theme.SuccessLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OverviewTab(
    user: User,
    cars: List<Car>,
    bookings: List<Booking>,
    customers: List<User>,
    onNavigate: (HomeTab) -> Unit,
) {
    val today = remember {
        SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
            .format(Date())
            .replaceFirstChar { it.uppercase() }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            GreetingCard(user = user, today = today)
        }

        item {
            StatsSection(user = user, cars = cars, bookings = bookings)
        }

        if (user.role == Role.CUSTOMER) {
            val activeBooking = bookings
                .filter { it.customerId == user.id }
                .firstOrNull { it.status == BookingStatus.ACTIVE }
            if (activeBooking != null) {
                item {
                    ActiveRentalCard(
                        booking = activeBooking,
                        car = cars.firstOrNull { it.id == activeBooking.carId },
                        onClick = { onNavigate(HomeTab.BOOKINGS) },
                    )
                }
            }
        }

        item {
            QuickActions(
                user = user,
                onNavigate = onNavigate,
            )
        }

        item {
            RecentBookings(
                user = user,
                cars = cars,
                bookings = bookings,
                customers = customers,
                onOpenAll = { onNavigate(HomeTab.BOOKINGS) },
            )
        }
    }
}

@Composable
private fun GreetingCard(user: User, today: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(BrandPrimary, BrandTeal),
                ),
            )
            .padding(20.dp),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(110.dp)
                .background(Color.White.copy(alpha = 0.07f), CircleShape),
        )

        Column {
            Surface(
                shape = RoundedCornerShape(50),
                color = Color.White.copy(alpha = 0.16f),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RoleBadge(role = user.role)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Akun aktif",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Halo, ${user.firstName}!",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = user.role.greeting,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.86f),
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = today,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.72f),
            )
        }
    }
}

@Composable
private fun StatsSection(
    user: User,
    cars: List<Car>,
    bookings: List<Booking>,
) {
    val availableCars = cars.count { it.status == com.adytransjaya.app.data.model.CarStatus.AVAILABLE }
    val rentedCars = cars.count { it.status == com.adytransjaya.app.data.model.CarStatus.RENTED }
    val pendingBookings = bookings.count { it.status == BookingStatus.PENDING }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (user.role == Role.OWNER) {
            RevenueCard()
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            when (user.role) {
                Role.OWNER -> {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Total mobil",
                        value = cars.size.toString(),
                        icon = Icons.Rounded.DirectionsCar,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Sedang disewa",
                        value = rentedCars.toString(),
                        icon = Icons.Rounded.EventAvailable,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }

                Role.EMPLOYEE -> {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Mobil tersedia",
                        value = availableCars.toString(),
                        icon = Icons.Rounded.DirectionsCar,
                        containerColor = SuccessLight,
                        contentColor = Success,
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Perlu konfirmasi",
                        value = pendingBookings.toString(),
                        icon = Icons.Rounded.TaskAlt,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                }

                Role.CUSTOMER -> {
                    val customerBookings = bookings.filter { it.customerId == user.id }
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Pesanan aktif",
                        value = customerBookings.count { it.status == BookingStatus.ACTIVE }.toString(),
                        icon = Icons.Rounded.ConfirmationNumber,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Perjalanan selesai",
                        value = customerBookings.count { it.status == BookingStatus.COMPLETED }.toString(),
                        icon = Icons.Rounded.CheckCircle,
                        containerColor = SuccessLight,
                        contentColor = Success,
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            when (user.role) {
                Role.OWNER -> {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Pesanan aktif",
                        value = bookings.count { it.status == BookingStatus.ACTIVE }.toString(),
                        icon = Icons.Rounded.ReceiptLong,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Pelanggan",
                        value = DemoUsers.customers.size.toString(),
                        icon = Icons.Rounded.Groups,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Role.EMPLOYEE -> {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Sedang disewa",
                        value = rentedCars.toString(),
                        icon = Icons.Rounded.EventAvailable,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Pelanggan",
                        value = DemoUsers.customers.size.toString(),
                        icon = Icons.Rounded.Groups,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Role.CUSTOMER -> {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Mobil tersedia",
                        value = availableCars.toString(),
                        icon = Icons.Rounded.DirectionsCar,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "Total transaksi",
                        value = formatCompactRupiah(
                            bookings
                                .filter {
                                    it.customerId == user.id &&
                                        it.status == BookingStatus.COMPLETED
                                }
                                .sumOf { it.total },
                        ),
                        icon = Icons.Rounded.AccountBalanceWallet,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun RevenueCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        BrandPrimary,
                        BrandTeal,
                    ),
                ),
            )
            .padding(18.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.AccountBalanceWallet,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pendapatan November 2026",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.82f),
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = formatRupiah(DemoData.currentMonthRevenue),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(50),
                color = Color.White.copy(alpha = 0.16f),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.TrendingUp,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "+10,6% dari bulan lalu",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(containerColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun ActiveRentalCard(
    booking: Booking,
    car: Car?,
    onClick: () -> Unit,
) {
    if (car == null) return

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Pesanan sedang berjalan",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                BookingStatusPill(status = booking.status)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
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
                        modifier = Modifier.size(26.dp),
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = car.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "${booking.startDate} – ${booking.endDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatCompactRupiah(booking.total),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "${booking.days} hari",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActions(
    user: User,
    onNavigate: (HomeTab) -> Unit,
) {
    val firstAction: Pair<String, Pair<ImageVector, HomeTab>>
    val secondAction: Pair<String, Pair<ImageVector, HomeTab>>

    when (user.role) {
        Role.OWNER -> {
            firstAction = "Lihat laporan" to (Icons.Rounded.AccountBalanceWallet to HomeTab.REPORT)
            secondAction = "Kelola mobil" to (Icons.Rounded.DirectionsCar to HomeTab.CARS)
        }

        Role.EMPLOYEE -> {
            firstAction = "Konfirmasi pesanan" to (Icons.Rounded.TaskAlt to HomeTab.BOOKINGS)
            secondAction = "Data pelanggan" to (Icons.Rounded.Groups to HomeTab.CUSTOMERS)
        }

        Role.CUSTOMER -> {
            firstAction = "Sewa mobil" to (Icons.Rounded.DirectionsCar to HomeTab.CARS)
            secondAction = "Pesanan saya" to (Icons.Rounded.ReceiptLong to HomeTab.BOOKINGS)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader(title = "Aksi cepat")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard(
                modifier = Modifier.weight(1f),
                label = firstAction.first,
                icon = firstAction.second.first,
                onClick = { onNavigate(firstAction.second.second) },
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                label = secondAction.first,
                icon = secondAction.second.first,
                onClick = { onNavigate(secondAction.second.second) },
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(11.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(19.dp),
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.weight(1f),
                maxLines = 2,
            )
            Icon(
                imageVector = Icons.Rounded.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun RecentBookings(
    user: User,
    cars: List<Car>,
    bookings: List<Booking>,
    customers: List<User>,
    onOpenAll: () -> Unit,
) {
    val recentBookings = when (user.role) {
        Role.CUSTOMER -> bookings.filter { it.customerId == user.id }
        else -> bookings
    }.take(3)

    Column {
        SectionHeader(
            title = if (user.role == Role.CUSTOMER) "Pesanan saya" else "Pesanan terbaru",
            actionLabel = "Lihat semua",
            onAction = onOpenAll,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
            if (recentBookings.isEmpty()) {
                EmptyState(
                    title = "Belum ada pesanan",
                    message = "Pesanan akan muncul di sini setelah dibuat.",
                )
            } else {
                Column {
                    recentBookings.forEachIndexed { index, booking ->
                        RecentBookingRow(
                            booking = booking,
                            car = cars.firstOrNull { it.id == booking.carId },
                            customerName = (
                                customers.firstOrNull { it.id == booking.customerId }
                                    ?: DemoData.customerById(booking.customerId)
                                )?.name,
                            showCustomer = user.role != Role.CUSTOMER,
                        )
                        if (index != recentBookings.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 14.dp),
                                color = MaterialTheme.colorScheme.outlineVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentBookingRow(
    booking: Booking,
    car: Car?,
    customerName: String?,
    showCustomer: Boolean,
) {
    val carName = car?.name ?: "Mobil dihapus"

    Row(
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = carName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = if (showCustomer && customerName != null) {
                    "$customerName · ${booking.code}"
                } else {
                    booking.code
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "${booking.startDate} – ${booking.endDate}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            BookingStatusPill(status = booking.status)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatCompactRupiah(booking.total),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
