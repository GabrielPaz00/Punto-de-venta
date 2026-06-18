package com.example.pointofsale.core.utils

import java.text.NumberFormat
import java.util.Locale

fun formatWithCommas(value: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    return formatter.format(value)
}