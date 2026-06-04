package com.example.pointofsale.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.lifecycle.ViewModel
import com.example.pointofsale.model.UserState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.ui.graphics.vector.ImageVector

class HomeViewModel():  ViewModel(){
    val userState = MutableStateFlow(UserState())

    private val allItems = listOf(
        Triple("Inicio", Icons.Default.Home, "home"),
        Triple("POS", Icons.Default.ShoppingCart, "pos"),
        Triple("Productos", Icons.Default.Inventory, "products"),
        Triple("Reportes", Icons.Default.BarChart, "reports"),
        Triple("Usuarios", Icons.Default.People, "users"),
        Triple("Perfil", Icons.Default.AccountCircle, "profile")
    )

    private val _filteredItems = MutableStateFlow<List<Triple<String, ImageVector, String>>>(emptyList())
    val filteredItems: StateFlow<List<Triple<String, ImageVector, String>>> = _filteredItems

    fun filterNavigationMenu() {
        val userLevel = userState.value.userLevel
        _filteredItems.value = allItems.filter { item ->
            when (userLevel) {
                "admin" -> true
                "special" -> item.first in listOf("Inicio", "Reportes", "Perfil")
                "user" -> item.first in listOf("Inicio", "POS", "Productos", "Perfil")
                else -> item.first == "Inicio"
            }
        }
    }
}