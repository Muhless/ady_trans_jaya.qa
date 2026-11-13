package com.adytransjaya.app.data

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Utilitas tanggal untuk pesanan.
 *
 * Tanggal disimpan sebagai teks `dd MMM yyyy` (contoh: `08 Nov 2026`) agar
 * konsisten dengan data demo. Perbandingan tanggal memakai epoch day supaya
 * pengecekan bentrok jadwal tidak bergantung pada format tampilan.
 */
object RentalDates {

    const val DAY_MILLIS: Long = 86_400_000L

    private val utc: TimeZone = TimeZone.getTimeZone("UTC")
    private val indonesian: Locale = Locale.forLanguageTag("id-ID")

    fun todayEpochDay(): Long {
        val local = Calendar.getInstance()
        val calendar = Calendar.getInstance(utc).apply {
            clear()
            set(
                local.get(Calendar.YEAR),
                local.get(Calendar.MONTH),
                local.get(Calendar.DAY_OF_MONTH),
            )
        }
        return calendar.timeInMillis / DAY_MILLIS
    }

    fun parseEpochDay(value: String): Long? {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return null

        val iso = runCatching {
            formatter("yyyy-MM-dd", Locale.US).parse(trimmed)
        }.getOrNull()
        if (iso != null) return iso.time / DAY_MILLIS

        val display = runCatching {
            formatter("dd MMM yyyy", indonesian).parse(trimmed)
        }.getOrNull()
        return display?.time?.let { it / DAY_MILLIS }
    }

    fun formatEpochDay(epochDay: Long): String =
        formatter("dd MMM yyyy", indonesian).format(Date(epochDay * DAY_MILLIS))

    fun millisFromEpochDay(epochDay: Long): Long = epochDay * DAY_MILLIS

    private fun formatter(pattern: String, locale: Locale): SimpleDateFormat =
        SimpleDateFormat(pattern, locale).apply {
            timeZone = utc
            isLenient = false
        }
}
