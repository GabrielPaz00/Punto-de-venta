package com.example.pointofsale.model.entities

data class SoldItem(
    val productId: String = "",
    val productName: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0,
    val subtotal: Double = 0.0
)