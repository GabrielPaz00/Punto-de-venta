package com.example.pointofsale.viewmodel.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pointofsale.model.entities.Product
import com.example.pointofsale.model.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch



class ProductViewModel(private val repository: ProductRepository = ProductRepository()) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    init {
        getAllProducts()
    }

    private fun getAllProducts() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                repository.getAllProductsFlow().collectLatest { products ->
                    _uiState.update { 
                        it.copy(
                            products = products,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
            } catch (e: Exception) {
                val errorMsg = e.cause?.message ?: e.message ?: "Error desconocido"
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = errorMsg
                    )
                }
            }
        }
    }

    fun searchProducts(query: String) {
        _uiState.update { it.copy(searchQuery = query, isLoading = true) }
        viewModelScope.launch {
            if (query.isEmpty()) {
                getAllProducts()
            } else {
                repository.searchProductsByName(query).collectLatest { products ->
                    _uiState.update { 
                        it.copy(
                            products = products,
                            isLoading = false
                        )
                    }
                }
            }
        }
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
