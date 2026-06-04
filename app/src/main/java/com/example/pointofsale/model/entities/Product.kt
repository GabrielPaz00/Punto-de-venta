package com.example.pointofsale.model.entities

import com.google.firebase.firestore.DocumentId


data class Product(
    @DocumentId val id: String = "",
    val name: String = "",
    val category: String = "",
    val price: Double = 0.0,
    val stock: Int = 0,
    val minimumStock: Int = 3,
)