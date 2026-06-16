package com.example.pointofsale.viewmodel.profile

data class ProfileUiState(
    val name: String = "",
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)