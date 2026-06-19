package com.example.pointofsale.viewmodel.sale

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pointofsale.model.entities.*
import com.example.pointofsale.model.repository.*
import com.example.pointofsale.viewmodel.home.HomeSummary
import com.google.firebase.Timestamp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class SaleViewModel(
    private val productRepository: ProductRepository = ProductRepository(),
    private val saleRepository: SaleRepository = SaleRepository.getInstance(),
    private val authRepository: AuthRepository = AuthRepository.getInstance()
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage


    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val products = _searchQuery
        .debounce(300L.milliseconds)
        .flatMapLatest { query ->
            if (query.isEmpty()) productRepository.getAllProductsFlow()
            else productRepository.searchProductsByName(query)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Cálculos del carrito
    val total = _cartItems.map { it.sumOf { item -> item.subtotal } }
    val iva = total.map { it * 0.16 }
    val subtotal = total.combine(iva) { t, i -> t - i }
    val cartBadgeCount = _cartItems.map { it.sumOf { item -> item.quantity } }

    fun onSearchQueryChange(query: String) { _searchQuery.value = query }

    fun searchProducts(query: String) {
        _searchQuery.value = query
    }

    fun addToCart(product: Product) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.product.id == product.id }
        
        if (index != -1) {
            val nextQuantity = current[index].quantity + 1
            if (nextQuantity <= product.stock) {
                current[index] = current[index].copy(quantity = nextQuantity)
                _cartItems.value = current
            } else {
                _errorMessage.value = "Stock insuficiente para ${product.name}"
            }
        } else {
            if (product.stock > 0) {
                current.add(CartItem(product, 1))
                _cartItems.value = current
            } else {
                _errorMessage.value = "Producto ${product.name} sin stock"
            }
        }
    }

    fun updateQuantity(item: CartItem, delta: Int) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOf(item)
        if (index != -1) {
            val newQty = current[index].quantity + delta
            if (newQty <= 0) {
                current.removeAt(index)
                _cartItems.value = current
            } else if (newQty <= item.product.stock) {
                current[index] = current[index].copy(quantity = newQty)
                _cartItems.value = current
            } else {
                _errorMessage.value = "No hay más stock disponible para ${item.product.name}"
            }
        }
    }

    fun removeProduct(item: CartItem) {
        _cartItems.value = _cartItems.value.filter { it.product.id != item.product.id }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearSuccess() {
        _successMessage.value = null
    }

    fun completeSale(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val currentTotal = _cartItems.value.sumOf { it.subtotal }
            val currentIva = currentTotal * 0.16
            val currentSubtotal = currentTotal - currentIva
            val sale = Sale(
                userId = authRepository.getCurrentUser()?.uid ?: "",
                saleDate = Timestamp.now(),
                subtotal = currentSubtotal,
                iva = currentIva,
                total = currentTotal,
                products = _cartItems.value.map {
                    SoldItem(it.product.id, it.product.name, it.quantity, it.product.price, it.subtotal)
                }
            )
            saleRepository.registerSale(sale).onSuccess {
                _cartItems.update { emptyList() }
                _successMessage.value = "Venta realizada con éxito"
                onSuccess()
            }.onFailure { error ->
                _errorMessage.value = error.message ?: "Error al registrar la venta"
            }
        }
    }
}
