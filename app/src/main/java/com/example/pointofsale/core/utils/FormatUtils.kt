package com.example.pointofsale.core.utils

import java.text.NumberFormat
import java.util.Locale

private val priceFormatter: NumberFormat by lazy {
    NumberFormat.getNumberInstance(Locale.US).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
}

fun formatWithCommas(value: Double): String {
    return priceFormatter.format(value)
}