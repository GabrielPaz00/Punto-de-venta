package com.example.pointofsale.viewmodel.product

import com.example.pointofsale.model.entities.Product
import com.example.pointofsale.model.repository.ProductRepository
import com.example.pointofsale.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var productRepository: ProductRepository
    private lateinit var viewModel: ProductViewModel

    @Before
    fun setup() {
        productRepository = mockk()
        // Mock default behavior for getAllProductsFlow
        coEvery { productRepository.getAllProductsFlow() } returns flowOf(emptyList())
        viewModel = ProductViewModel(productRepository)
    }

    @Test
    fun `createProduct llama al repositorio correctamente`() = runTest {
        // Given
        val product = Product(id = "1", name = "Test Product", price = 10.0, stock = 5)
        coEvery { productRepository.createProduct(product) } returns Result.success(true)

        // When
        viewModel.createProduct(product)

        // Then
        coVerify(exactly = 1) { productRepository.createProduct(product) }
    }

    @Test
    fun `updateStock llama al repositorio con el nuevo stock`() = runTest {
        // Given
        val productId = "prod123"
        val newStock = 20
        coEvery { productRepository.updateStock(productId, newStock) } returns Result.success(true)

        // When
        viewModel.updateStock(productId, newStock)

        // Then
        coVerify(exactly = 1) { productRepository.updateStock(productId, newStock) }
    }

    @Test
    fun `searchProducts actualiza la query de busqueda`() = runTest {
        //Given
        val searchQuery = "Laptop"

        // When
        viewModel.searchProducts(searchQuery)

        // Then
        assertEquals(searchQuery, viewModel.searchQuery.value)
    }
}
