package com.example.pointofsale.view.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pointofsale.core.components.BottomNavBar
import com.example.pointofsale.core.theme.PointOfSaleTheme
import com.example.pointofsale.viewmodel.home.HomeViewModel

@Composable
fun HomeView(viewModel: HomeViewModel, userLevel: String) {
    Scaffold(
        bottomBar = { BottomNavBar(viewModel) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            HeaderSection(userLevel)
            Spacer(modifier = Modifier.height(24.dp))
            SummarySection()
            Spacer(modifier = Modifier.height(32.dp))
            QuickAccessSection()
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun HeaderSection(roleName: String) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 40.dp)
        ) {
            Text(
                text = "Hola, $roleName",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "martes, 26 de mayo de 2026",
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun SummarySection() {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(
            text = "Resumen de Hoy",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Ventas del Día",
                value = "$1,234.50",
                icon = Icons.Default.AttachMoney,
                iconColor = MaterialTheme.colorScheme.primary
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Productos Vendidos",
                value = "48",
                icon = Icons.Default.Inventory2,
                iconColor = MaterialTheme.colorScheme.secondary
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Stock Bajo",
                value = "12",
                icon = Icons.AutoMirrored.Filled.TrendingDown,
                iconColor = Color(0xFFF44336)
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Clientes",
                value = "23",
                icon = Icons.Default.Group,
                iconColor = Color(0xFF2196F3)
            )
        }
    }
}

@Composable
fun StatCard(modifier: Modifier, title: String, value: String, icon: ImageVector, iconColor: Color) {
    Card(
        modifier = modifier.aspectRatio(0.85f),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                color = iconColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = value,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun QuickAccessSection() {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(
            text = "Accesos Rápidos",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )
        QuickAccessButton(
            title = "Nueva Venta",
            subtitle = "Procesar una venta rápida",
            icon = Icons.Default.AttachMoney,
            containerColor = Color(0xFFA076F9),
            contentColor = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))
        QuickAccessButton(
            title = "Agregar Producto",
            subtitle = "Registrar nuevo inventario",
            icon = Icons.Default.Inventory2,
            containerColor = Color(0xFF26C6DA),
            contentColor = Color.Black
        )
    }
}

@Composable
fun QuickAccessButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(32.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = contentColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = subtitle, color = contentColor.copy(alpha = 0.6f), fontSize = 14.sp)
            }
            Surface(
                color = Color.Black.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}



@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun HomeViewPreview() {
    PointOfSaleTheme(darkTheme = false) {
        HomeView(viewModel {
            HomeViewModel()
        },
        userLevel = "user")
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun HomeViewDarkPreview() {
    PointOfSaleTheme(darkTheme = true) {
        HomeView(viewModel {
            HomeViewModel()
        },
        userLevel = "admin")
    }
}
