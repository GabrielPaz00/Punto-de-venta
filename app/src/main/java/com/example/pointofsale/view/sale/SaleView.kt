package com.example.pointofsale.view.sale

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pointofsale.core.components.Header
import com.example.pointofsale.core.components.ProductCard
import com.example.pointofsale.viewmodel.sale.SaleViewModel
import androidx.compose.runtime.LaunchedEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleView(viewModel: SaleViewModel = viewModel()) {
    val products by viewModel.products.collectAsState(initial = emptyList())
    val cartItems by viewModel.cartItems.collectAsState(initial = emptyList())
    val total by viewModel.total.collectAsState(initial = 0.0)
    val subtotal by viewModel.subtotal.collectAsState(initial = 0.0)
    val iva by viewModel.iva.collectAsState(initial = 0.0)
    val badgeCount by viewModel.cartBadgeCount.collectAsState(initial = 0)
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    
    val context = LocalContext.current
    var showCart by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            Toast.makeText(context, "$it  ✅ ", Toast.LENGTH_SHORT).show()
            viewModel.clearSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Header(
            title = "Punto de venta",
            icon = Icons.Default.ShoppingCart,
            entityCount = products.size,
            searchQuery = searchQuery,
            onSearchQueryChange = { viewModel.searchProducts(it) },
            onAddClick = { showCart = true },
            badgeCount = badgeCount
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(products) { product ->
                ProductCard(
                    product = product,
                    action = {
                        IconButton(
                            onClick = { viewModel.addToCart(product) },
                            modifier = Modifier.clip(MaterialTheme.shapes.medium).background(MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                )
            }
        }
    }

    if (showCart) {
        ModalBottomSheet(
            onDismissRequest = { showCart = false },
            sheetState = sheetState
        ) {
            CartContent(
                items = cartItems,
                subtotal = subtotal,
                total = total,
                iva = iva,
                onUpdateQty = viewModel::updateQuantity,
                onRemove = viewModel::removeProduct,
                onCheckout = { viewModel.completeSale { showCart = false } }
            )
        }
    }
}



