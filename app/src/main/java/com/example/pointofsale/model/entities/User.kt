package com.example.pointofsale.model.entities

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val isActive: Boolean = true,
    val userLevel: String = ""
)
