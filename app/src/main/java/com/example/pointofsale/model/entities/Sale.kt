package com.example.pointofsale.model.entities

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Sale(
    @DocumentId val id: String = "",
    val cashierId: String = "",
    val saleDate: Timestamp = Timestamp.now(),
    val subtotal: Double = 0.0,
    val iva: Double = 0.0,
    val total: Double = 0.0,
    val products: List<SoldItem> = emptyList()
)


