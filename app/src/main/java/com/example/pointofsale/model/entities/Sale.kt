package com.example.pointofsale.model.entities

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Sale(
    @DocumentId val id: String = "",
    val userId: String = "",
    val saleDate: Timestamp = Timestamp.now(),
    val total: Double = 0.0,
    val iva: Double = 0.0,
    val subtotal: Double = 0.0,
    val products: List<SoldItem> = emptyList()
)


