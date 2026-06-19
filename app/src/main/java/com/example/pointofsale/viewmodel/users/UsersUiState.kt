package com.example.pointofsale.viewmodel.users

import com.example.pointofsale.model.entities.User

data class UsersUiState(
    val isLoading: Boolean = true,
    val usersList: List<User> = emptyList(),
    val errorMessage: String? = null,
    val isUpdateSuccess: Boolean = false
)
