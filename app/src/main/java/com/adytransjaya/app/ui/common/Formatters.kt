package com.adytransjaya.app.ui.common

import java.text.NumberFormat
import java.util.Locale

private val idLocale = Locale("id", "ID")
private val numberFormat = NumberFormat.getNumberInstance(idLocale)

fun formatRupiah(amount: Long): String = "Rp ${numberFormat.format(amount)}"

fun formatCompactRupiah(amount: Long): String = when {
    amount >= 1_000_000_000 -> {
        val value = amount / 1_000_000_000.0
        "Rp ${trimDecimal(value)} M"
    }

    amount >= 1_000_000 -> {
        val value = amount / 1_000_000.0
        "Rp ${trimDecimal(value)} jt"
    }

    amount >= 1_000 -> {
        val value = amount / 1_000.0
        "Rp ${trimDecimal(value)} rb"
    }

    else -> "Rp $amount"
}

private fun trimDecimal(value: Double): String {
    val rounded = (value * 10).toInt() / 10.0
    return if (rounded % 1.0 == 0.0) {
        rounded.toInt().toString()
    } else {
        String.format(idLocale, "%.1f", rounded)
    }
}
