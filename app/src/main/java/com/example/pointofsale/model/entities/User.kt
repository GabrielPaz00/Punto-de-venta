package com.example.pointofsale.model.entities

data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val isActive: Boolean = true,
    val userLevel: String = "admin"
)
