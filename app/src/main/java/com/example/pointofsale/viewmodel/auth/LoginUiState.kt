package com.example.pointofsale.viewmodel.auth
import com.example.pointofsale.model.entities.User


data class LoginUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val userProfile: User? = null,
    val errorMessage: String? = null
)