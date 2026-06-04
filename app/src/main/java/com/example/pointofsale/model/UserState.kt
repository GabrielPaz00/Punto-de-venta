package com.example.pointofsale.model

data class UserState(
    val username: String = "",
    val isLogged: Boolean = true,
    val userLevel: String = "admin"
)
