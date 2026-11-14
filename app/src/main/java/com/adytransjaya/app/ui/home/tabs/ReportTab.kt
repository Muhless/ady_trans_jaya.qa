package com.adytransjaya.app.ui.home.tabs

import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.TrendingDown
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.adytransjaya.app.data.RentalDates
import com.adytransjaya.app.data.model.Booking
import com.adytransjaya.app.data.model.BookingStatus
import com.adytransjaya.app.data.model.Car
import com.adytransjaya.app.data.model.CarStatus
import com.adytransjaya.app.data.model.PaymentStatus
import com.adytransjaya.app.data.model.User
import com.adytransjaya.app.ui.common.SectionHeader
import com.adytransjaya.app.ui.common.formatCompactRupiah
import com.adytransjaya.app.ui.common.formatRupiah
import com.adytransjaya.app.ui.theme.BrandPrimary
import com.adytransjaya.app.ui.theme.BrandTeal
import com.adytransjaya.app.ui.theme.Success
import com.adytransjaya.app.ui.theme.SuccessLight
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private const val REPORT_MONTH_COUNT = 6

/**
 * Laporan dihitung dari pesanan berstatus `Selesai`, bukan data statis,
 * sehingga angka ikut berubah ketika staf menyelesaikan pesanan.
 */
@Composable
fun ReportTab(
    cars: List<Car>,
    bookings: List<Booking>,
    customers: List<User>,
) {
    val context = LocalContext.current
    val completed = remember(bookings) {
        bookings.filter { it.status == BookingStatus.COMPLETED }
    }
    val monthlyRevenue = remember(completed) {
        buildMonthlyRevenue(completed, REPORT_MONTH_COUNT)
    }
    val maxRevenue = monthlyRevenue
        .maxOfOrNull { it.amount }
        ?.coerceAtLeast(1L)
        ?.toFloat()
        ?: 1f
    val currentMonth = monthlyRevenue.last()
    val previousMonth = monthlyRevenue.dropLast(1).lastOrNull()
    val growthPercent = if (previousMonth != null && previousMonth.amount > 0) {
        (currentMonth.amount - previousMonth.amount) * 100.0 / previousMonth.amount
    } else {
        null
    }
    val totalSixMonths = monthlyRevenue.sumOf { it.amount }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column {
                Text(
                    text = "Laporan & pendapatan",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.testTag("report_title"),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Dihitung dari ${completed.size} pesanan selesai",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.testTag("report_completed_count"),
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = {
                        exportCompletedBookingsCsv(
                            context = context,
                            bookings = completed,
                            cars = cars,
                            customers = customers,
                        ).onSuccess { file ->
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                file,
                            )
                            val share = Intent(Intent.ACTION_SEND).apply {
                                type = "text/csv"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                putExtra(
                                    Intent.EXTRA_SUBJECT,
                                    "Laporan Ady Trans Jaya",
                                )
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(
                                Intent.createChooser(share, "Bagikan laporan CSV"),
                            )
                        }.onFailure { error ->
                            Toast.makeText(
                                context,
                                error.message ?: "Gagal mengekspor laporan.",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    },
                    modifier = Modifier.testTag("report_export"),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.FileDownload,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ekspor CSV")
                }
            }
        }

        item {
            RevenueSummaryCard(
                monthLabel = currentMonth.labelLong,
                revenue = currentMonth.amount,
                growthPercent = growthPercent,
                totalSixMonths = totalSixMonths,
            )
        }

        item {
            Surface(
                modifier = Modifier.testTag("report_trend"),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionHeader(title = "Tren pendapatan 6 bulan")
                    Spacer(modifier = Modifier.height(18.dp))
                    if (totalSixMonths == 0L) {
                        Text(
                            text = "Belum ada pendapatan dari pesanan selesai.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(170.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Bottom,
                        ) {
                            monthlyRevenue.forEach { item ->
                                val ratio = (item.amount / maxRevenue).coerceIn(0.08f, 1f)
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Text(
                                        text = formatCompactRupiah(item.amount)
                                            .removePrefix("Rp ")
                                            .replace(" jt", "jt"),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Box(
                                        modifier = Modifier
                                            .height((96f * ratio).dp)
                                            .fillMaxWidth(0.55f)
                                            .clip(
                                                RoundedCornerShape(
                                                    topStart = 7.dp,
                                                    topEnd = 7.dp,
                                                ),
                                            )
                                            .background(
                                                Brush.verticalGradient(
                                                    colors = listOf(
                                                        MaterialTheme.colorScheme.primary,
                                                        BrandTeal,
                                                    ),
                                                ),
                                            ),
                                    )
                                    Spacer(modifier = Modifier.height(7.dp))
                                    Text(
                                        text = item.labelShort,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            TopCarsCard(cars = cars, bookings = bookings)
        }

        item {
            OperationalSummaryCard(cars = cars, bookings = bookings)
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun RevenueSummaryCard(
    monthLabel: String,
    revenue: Long,
    growthPercent: Double?,
    totalSixMonths: Long,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("report_revenue_card")
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
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color.White.copy(alpha = 0.16f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AccountBalanceWallet,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(21.dp),
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Pendapatan $monthLabel",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.9f),
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = formatRupiah(revenue),
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.16f),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = if ((growthPercent ?: 0.0) >= 0) {
                                Icons.Rounded.TrendingUp
                            } else {
                                Icons.Rounded.TrendingDown
                            },
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = growthPercent?.let {
                                val sign = if (it >= 0) "+" else ""
                                "$sign${String.format(Locale.forLanguageTag("id-ID"), "%.1f", it)}%"
                            } ?: "Belum ada pembanding",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.16f),
                ) {
                    Text(
                        text = "Total 6 bulan: ${formatCompactRupiah(totalSixMonths)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun TopCarsCard(
    cars: List<Car>,
    bookings: List<Booking>,
) {
    val revenueByCar = bookings
        .filter { it.status == BookingStatus.COMPLETED }
        .groupBy { it.carId }
        .mapValues { entry -> entry.value.sumOf { it.total } }
        .toList()
        .sortedByDescending { it.second }
    val maxRevenue = revenueByCar.maxOfOrNull { it.second }?.coerceAtLeast(1L)?.toFloat() ?: 1f

    Surface(
        modifier = Modifier.testTag("report_top_cars"),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionHeader(title = "Pendapatan per mobil")
            Spacer(modifier = Modifier.height(14.dp))

            if (revenueByCar.isEmpty()) {
                Text(
                    text = "Belum ada transaksi selesai.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                revenueByCar.forEachIndexed { index, (carId, revenue) ->
                    val carName = cars.firstOrNull { it.id == carId }?.name
                        ?: "Mobil dihapus"
                    val ratio = (revenue / maxRevenue).coerceIn(0.05f, 1f)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = carName,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                        )
                        Text(
                            text = formatCompactRupiah(revenue),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { ratio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(RoundedCornerShape(50)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                    if (index != revenueByCar.lastIndex) {
                        Spacer(modifier = Modifier.height(13.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun OperationalSummaryCard(
    cars: List<Car>,
    bookings: List<Booking>,
) {
    val completedBookings = bookings.filter { it.status == BookingStatus.COMPLETED }
    val completed = completedBookings.size
    val cancelled = bookings.count { it.status == BookingStatus.CANCELLED }
    val totalCars = cars.size
    val available = cars.count { it.status == CarStatus.AVAILABLE }
    val utilization = if (totalCars == 0) 0 else ((totalCars - available) * 100) / totalCars
    val averageDays = if (completed == 0) {
        0.0
    } else {
        completedBookings.sumOf { it.days }.toDouble() / completed
    }
    val revenuePerBooking = if (completed == 0) {
        0L
    } else {
        completedBookings.sumOf { it.total } / completed
    }
    val unpaidCompleted = completedBookings.count { it.paymentStatus != PaymentStatus.PAID }
    val outstanding = completedBookings.sumOf {
        (it.total - it.paidAmount).coerceAtLeast(0L)
    }
    val idLocale = Locale.forLanguageTag("id-ID")

    Surface(
        modifier = Modifier.testTag("report_operations"),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionHeader(title = "Ringkasan operasional")
            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(SuccessLight, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DirectionsCar,
                        contentDescription = null,
                        tint = Success,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Utilisasi armada",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = "$available dari $totalCars mobil tersedia",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = "$utilization%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 14.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
            )

            SummaryLine(label = "Pesanan selesai", value = "$completed pesanan")
            Spacer(modifier = Modifier.height(10.dp))
            SummaryLine(label = "Pesanan dibatalkan", value = "$cancelled pesanan")
            Spacer(modifier = Modifier.height(10.dp))
            SummaryLine(
                label = "Rata-rata durasi sewa",
                value = if (completed == 0) {
                    "Belum ada data"
                } else {
                    String.format(idLocale, "%.1f hari", averageDays)
                },
            )
            Spacer(modifier = Modifier.height(10.dp))
            SummaryLine(
                label = "Rata-rata per pesanan",
                value = if (completed == 0) "Belum ada data" else formatCompactRupiah(revenuePerBooking),
            )
            Spacer(modifier = Modifier.height(10.dp))
            SummaryLine(
                label = "Pesanan selesai belum lunas",
                value = "$unpaidCompleted pesanan",
            )
            Spacer(modifier = Modifier.height(10.dp))
            SummaryLine(
                label = "Piutang berjalan",
                value = formatCompactRupiah(outstanding),
            )
        }
    }
}

@Composable
private fun SummaryLine(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Rounded.Payments,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(15.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

private data class MonthBucket(
    val labelShort: String,
    val labelLong: String,
    val amount: Long,
)

private fun buildMonthlyRevenue(
    completed: List<Booking>,
    monthCount: Int,
): List<MonthBucket> {
    val locale = Locale.forLanguageTag("id-ID")
    val utc = TimeZone.getTimeZone("UTC")
    val today = RentalDates.todayEpochDay()
    val todayCalendar = Calendar.getInstance(utc).apply {
        timeInMillis = today * RentalDates.DAY_MILLIS
    }
    val buckets = linkedMapOf<Pair<Int, Int>, Long>()
    val months = mutableListOf<Pair<Int, Int>>()

    repeat(monthCount) { offset ->
        val calendar = Calendar.getInstance(utc).apply {
            clear()
            set(
                todayCalendar.get(Calendar.YEAR),
                todayCalendar.get(Calendar.MONTH),
                1,
            )
            add(Calendar.MONTH, -(monthCount - 1 - offset))
        }
        val key = calendar.get(Calendar.YEAR) to calendar.get(Calendar.MONTH)
        months += key
        buckets[key] = 0L
    }

    completed.forEach { booking ->
        val epochDay = RentalDates.parseEpochDay(booking.startDate) ?: return@forEach
        val calendar = Calendar.getInstance(utc).apply {
            timeInMillis = epochDay * RentalDates.DAY_MILLIS
        }
        val key = calendar.get(Calendar.YEAR) to calendar.get(Calendar.MONTH)
        if (buckets.containsKey(key)) {
            buckets[key] = buckets.getValue(key) + booking.total
        }
    }

    return months.map { (year, month) ->
        val calendar = Calendar.getInstance(utc).apply {
            clear()
            set(year, month, 1)
        }
        val date = Date(calendar.timeInMillis)
        MonthBucket(
            labelShort = SimpleDateFormat("MMM", locale).format(date),
            labelLong = SimpleDateFormat("MMMM yyyy", locale).format(date),
            amount = buckets.getValue(year to month),
        )
    }
}

/**
 * Menulis laporan pesanan selesai ke file CSV di cache, lalu file tersebut
 * dibagikan lewat Intent supaya bisa disimpan atau dikirim ke aplikasi lain.
 */
private fun exportCompletedBookingsCsv(
    context: Context,
    bookings: List<Booking>,
    cars: List<Car>,
    customers: List<User>,
): Result<File> = runCatching {
    val directory = File(context.cacheDir, "exports").apply { mkdirs() }
    val timestamp = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())
    val file = File(directory, "laporan-ady-trans-jaya-$timestamp.csv")

    val header = listOf(
        "Kode",
        "Tanggal Mulai",
        "Tanggal Selesai",
        "Durasi (hari)",
        "Mobil",
        "Plat",
        "Pelanggan",
        "Total",
        "Dibayar",
        "Sisa",
        "Status Pesanan",
        "Status Pembayaran",
    )
    val rows = bookings.sortedBy { it.code }.map { booking ->
        val car = cars.firstOrNull { it.id == booking.carId }
        val customer = customers.firstOrNull { it.id == booking.customerId }
        listOf(
            booking.code,
            booking.startDate,
            booking.endDate,
            booking.days.toString(),
            car?.name ?: "Mobil dihapus",
            car?.plate ?: "-",
            customer?.name ?: "Pelanggan tidak ditemukan",
            booking.total.toString(),
            booking.paidAmount.toString(),
            (booking.total - booking.paidAmount).coerceAtLeast(0L).toString(),
            booking.status.label,
            booking.paymentStatus.label,
        )
    }

    val content = buildString {
        append('\uFEFF')
        appendLine(header.joinToString(",") { csvEscape(it) })
        rows.forEach { row ->
            appendLine(row.joinToString(",") { value -> csvEscape(value) })
        }
    }
    file.writeText(content)
    file
}

private fun csvEscape(value: String): String {
    val needsQuotes = value.any { it == ',' || it == '"' || it == '\n' || it == '\r' }
    return if (needsQuotes) {
        "\"" + value.replace("\"", "\"\"") + "\""
    } else {
        value
    }
}
