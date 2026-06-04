package com.example.pointofsale.model.entities

data class CartItem(
    val product: Product = Product(),
    var quantity: Int = 1
) {
    val subtotal: Double
        get() = product.price * quantity
}