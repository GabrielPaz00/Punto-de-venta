package com.example.pointofsale.core.utils

import com.example.pointofsale.R

object CategoryUtils {
    const val CATEGORY_LAPTOP = "Laptop"
    const val CATEGORY_KEYBOARD = "Teclado"
    const val CATEGORY_AUDIO = "Audio"
    const val CATEGORY_MOUSE = "Mouse"
    const val CATEGORY_MONITOR = "Monitor"

    val categories = listOf(
        CATEGORY_LAPTOP,
        CATEGORY_KEYBOARD,
        CATEGORY_AUDIO,
        CATEGORY_MOUSE,
        CATEGORY_MONITOR
    )

    fun getCategoryImage(category: String): Int {
        return when (category) {
            CATEGORY_LAPTOP -> R.drawable.ic_laptop
            CATEGORY_KEYBOARD -> R.drawable.ic_keyboard
            CATEGORY_AUDIO -> R.drawable.ic_audio
            CATEGORY_MOUSE -> R.drawable.ic_mouse
            CATEGORY_MONITOR -> R.drawable.ic_monitor
            else -> R.drawable.ic_inventory_2 // Fallback to inventory icon if not found
        }
    }
}
