package com.stardust.cetccalc.util

import java.text.NumberFormat
import java.util.Locale

private val arabicLocale: Locale = Locale.forLanguageTag("ar")

/** Formats a money amount with Arabic-Indic digits and the EGP suffix, e.g. ٤٬٢٧٥ ج.م */
fun formatMoney(value: Double): String {
    val nf = NumberFormat.getNumberInstance(arabicLocale).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 0
    }
    return nf.format(value) + " ج.م"
}

/** Formats an integer quantity with Arabic-Indic digits. */
fun formatCount(value: Int): String {
    val nf = NumberFormat.getNumberInstance(arabicLocale).apply {
        maximumFractionDigits = 0
    }
    return nf.format(value)
}

/** Accepts both Latin and Arabic-Indic digits from the keyboard. */
fun parseLocalizedInt(text: String): Int? {
    val normalized = text.trim().map { ch ->
        when (ch) {
            in '٠'..'٩' -> '0' + (ch - '٠')
            in '۰'..'۹' -> '0' + (ch - '۰')
            else -> ch
        }
    }.joinToString("")
    if (normalized.isEmpty()) return null
    if (!normalized.all { it.isDigit() }) return null
    return normalized.toIntOrNull()
}
