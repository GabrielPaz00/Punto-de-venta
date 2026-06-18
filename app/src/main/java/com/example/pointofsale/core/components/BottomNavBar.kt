package com.example.pointofsale.core.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun BottomNavBar(
    userLevel: String,
    navController: NavController
) {
    val allItems = listOf(
        NavItem("Inicio", Icons.Default.Home, "home"),
        NavItem("POS", Icons.Default.ShoppingCart, "pos"),
        NavItem("Productos", Icons.Default.Inventory, "products"),
    //    NavItem("Reportes", Icons.Default.BarChart, "reports"),
        NavItem("Usuarios", Icons.Default.People, "users"),
        NavItem("Perfil", Icons.Default.AccountCircle, "profile")
    )

    val filteredItems = remember(userLevel) {
        allItems.filter { item ->
            when (userLevel) {
                "admin" -> true
                "user" -> item.label in listOf("Inicio", "POS", "Productos", "Perfil")
                else -> item.label == "Inicio"
            }
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val surfaceBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)

    NavigationBar(
        modifier = Modifier.drawWithContent {
            drawContent()
            drawLine(
                color = surfaceBorderColor,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 0.2.dp.toPx()
            )
        },
        containerColor = Color.Transparent,
        tonalElevation = 8.dp
    ) {
        filteredItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label, fontSize = 10.sp) },
                selected = currentRoute?.contains(item.route) == true,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
