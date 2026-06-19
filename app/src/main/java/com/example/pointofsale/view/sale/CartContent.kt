package com.example.pointofsale.view.sale


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pointofsale.core.utils.formatWithCommas
import com.example.pointofsale.model.entities.CartItem

@Composable
fun CartContent(
    items: List<CartItem>,
    subtotal: Double,
    total: Double,
    iva: Double,
    onUpdateQty: (CartItem, Int) -> Unit,
    onRemove: (CartItem) -> Unit,
    onCheckout: () -> Unit
) {



    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(20.dp)
    ) {
        // Título del Carrito
        Text(
            text = "Carrito de Compra",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Revisa y completa tu compra",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Lista de productos en el carrito
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                CartItemRow(item, onUpdateQty, onRemove)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = Color.DarkGray, thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(16.dp))

        // Resumen de costos
        SummaryRow(label = "Subtotal", value = subtotal)
        SummaryRow(label = "IVA (16%)", value = iva)

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Total", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
            Text(
                text =  formatWithCommas(total),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFBB86FC)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón de Cobrar (Color Cyan como en la imagen)
        Button(
            onClick = onCheckout,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Cobrar ${formatWithCommas(total)}",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onUpdateQty: (CartItem, Int) -> Unit,
    onRemove: (CartItem) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Miniatura
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Color.White,
                        androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.DarkGray)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.product.name, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
                Text("${formatWithCommas(item.product.price)} c/u", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text(formatWithCommas(item.subtotal), color = Color(0xFFBB86FC), fontWeight = FontWeight.SemiBold)
            }

            // Controles de cantidad
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        Color.DarkGray.copy(alpha = 0.5f),
                        androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 4.dp)
            ) {
                IconButton(onClick = { onUpdateQty(item, -1) }, modifier = Modifier.size(
                    32.dp
                )) {
                    Icon(Icons.Default.Remove, contentDescription = null, tint = Color.White, modifier = Modifier.size(
                        16.dp
                    ))
                }
                Text(item.quantity.toString(), modifier = Modifier.padding(
                    horizontal = 8.dp
                ), color = Color.White, fontWeight = FontWeight.Bold)
                IconButton(onClick = { onUpdateQty(item, 1) }, modifier = Modifier.size(
                    32.dp
                )) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(
                        16.dp
                    ))
                }
            }

            IconButton(onClick = { onRemove(item) }) {
                Icon(Icons.Default.Close, contentDescription = "Eliminar", tint = Color.Gray, modifier = Modifier.size(
                    20.dp
                ))
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray)
        Text(formatWithCommas(value), color = Color.White)
    }
}