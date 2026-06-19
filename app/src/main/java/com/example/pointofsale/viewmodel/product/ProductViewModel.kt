package com.example.pointofsale.viewmodel.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pointofsale.model.entities.Product
import com.example.pointofsale.model.repository.ProductRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: ProductRepository = ProductRepository()) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    private val _isLoading = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _products = _searchQuery
        .flatMapLatest { query ->
            if (query.isEmpty()) {
                repository.getAllProductsFlow()
            } else {
                repository.searchProductsByName(query)
            }
        }
        .onStart { _isLoading.value = true }
        .map { products ->
            _isLoading.value = false
            _errorMessage.value = null
            products
        }
        .catch { e ->
            _isLoading.value = false
            _errorMessage.value = e.message ?: "Error desconocido"
            emit(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val uiState: StateFlow<ProductUiState> = combine(
        _products,
        _searchQuery,
        _isLoading,
        _errorMessage
    ) { products, query, loading, error ->
        ProductUiState(
            products = products,
            searchQuery = query,
            isLoading = loading,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProductUiState(isLoading = true)
    )

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    fun searchProducts(query: String) {
        _searchQuery.value = query
    }

    fun getProduct(id: String) {
        viewModelScope.launch {
            repository.getProductById(id).onSuccess { product ->
                _selectedProduct.value = product
            }.onFailure {
                _selectedProduct.value = null
            }
        }
    }

    fun createProduct(product: Product) {
        viewModelScope.launch {
            repository.createProduct(product)
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
        }
    }

    fun updateStock(productId: String, newStock: Int) {
        viewModelScope.launch {
            repository.updateStock(productId, newStock)
        }
    }

    fun clearSelectedProduct() {
        _selectedProduct.value = null
    }
}
