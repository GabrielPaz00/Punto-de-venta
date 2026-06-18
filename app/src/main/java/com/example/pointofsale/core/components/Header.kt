package com.example.pointofsale.core.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pointofsale.core.theme.PointOfSaleTheme
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset


@Composable
fun Header(
    title: String,
    icon: ImageVector,
    entityCount: Int,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onAddClick: () -> Unit
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .drawWithContent {
                drawContent()
                drawLine(
                    color = surfaceColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 2.dp.toPx()
                )
            },
        color = Color.Transparent,
        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),

    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    val subtitle = "$entityCount ${title.lowercase()} registrados"
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                }

                Surface(
                    onClick = onAddClick,
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
                    modifier = Modifier.size(48.dp)
                ) {
                    val descriptionIcon = "Agregar ${title.lowercase()}"
                    Icon(
                        imageVector = icon,
                        contentDescription = descriptionIcon,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                placeholder = {
                    Text(
                        "Buscar ${title.lowercase()}...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    // Fondo sutil (surfaceVariant es perfecto para esto)
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),

                    // El borde en la imagen es casi invisible o muy tenue
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),

                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                ),
                singleLine = true
            )
        }
    }
}
@Preview(showBackground = true, name = "Light Mode")
@Composable
fun HeaderPreviewLight() {
    PointOfSaleTheme(darkTheme = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            HeaderPreviewContent()
        }
    }
}

@Preview(showBackground = true, name = "Dark Mode", backgroundColor = 0xFF121212)
@Composable
fun HeaderPreviewDark() {
    PointOfSaleTheme(darkTheme = true) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            HeaderPreviewContent()
        }
    }
}
@Composable
fun HeaderPreviewContent() {
    Column {
        // Ejemplo para Productos
        Header(
            title = "Productos",
            icon = Icons.Default.Add,
            entityCount = 15,
            searchQuery = "",
            onSearchQueryChange = {},
            onAddClick = {},
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Ejemplo para Usuarios (con título y subtítulo que pediste)
        Header(
            title = "Usuarios",
            icon = Icons.Default.PersonAdd,
            entityCount = 25,
            searchQuery = "",
            onSearchQueryChange = {},
            onAddClick = {},
        )
    }
}