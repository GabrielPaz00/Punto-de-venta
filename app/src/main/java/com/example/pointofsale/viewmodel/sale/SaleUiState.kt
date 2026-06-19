package com.example.pointofsale.viewmodel.sale

import com.example.pointofsale.model.entities.CartItem

data class SaleUiState(
    val cartItems: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val itemCount: Int = 0,
)

