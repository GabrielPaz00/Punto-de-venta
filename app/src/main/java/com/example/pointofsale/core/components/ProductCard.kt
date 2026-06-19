package com.example.pointofsale.core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pointofsale.core.utils.CategoryUtils
import com.example.pointofsale.core.utils.formatWithCommas
import com.example.pointofsale.model.entities.Product
import com.google.firebase.firestore.Query

@Composable
fun ProductCard(
    product: Product,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    action: @Composable (RowScope.() -> Unit)? = null,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono / Imagen de Categoría
            Box(
                modifier = Modifier.size(64.dp).clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = CategoryUtils.getCategoryImage(product.category)),
                    contentDescription = product.category,
                    modifier = Modifier.size(48.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (product.stock <= 5) {
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Default.Warning, "Bajo stock", tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                    }
                }
                Text(product.category, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                Spacer(modifier = Modifier.height(4.dp))
                Text("$${formatWithCommas(product.price)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }

            // Slot para botón o badge
            action?.invoke(this)
        }
    }
}

@Composable
fun StockBadge(stock: Int) {
    val isLow = stock <= 5
    val contentColor = if (isLow) Color(0xFFFFB74D) else Color(0xFF81C784)
    Surface(
        color = contentColor.copy(alpha = 0.12f),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    ) {
        Text(
            text = "$stock unidades",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            fontWeight = FontWeight.Bold
        )
    }
}
