package com.example.pointofsale.viewmodel.product

import com.example.pointofsale.model.entities.Product

data class ProductUiState(
    val products: List<Product> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
