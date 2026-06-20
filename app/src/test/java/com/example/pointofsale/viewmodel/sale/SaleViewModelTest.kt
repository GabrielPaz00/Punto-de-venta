package com.example.pointofsale.viewmodel.sale

import com.example.pointofsale.model.entities.CartItem
import com.example.pointofsale.model.entities.Product
import com.example.pointofsale.model.repository.AuthRepository
import com.example.pointofsale.model.repository.ProductRepository
import com.example.pointofsale.model.repository.SaleRepository
import com.example.pointofsale.util.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SaleViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var productRepository: ProductRepository
    private lateinit var saleRepository: SaleRepository
    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: SaleViewModel

    @Before
    fun setup() {
        productRepository = mockk()
        saleRepository = mockk()
        authRepository = mockk()
        
        every { productRepository.getAllProductsFlow() } returns flowOf(emptyList())
        
        viewModel = SaleViewModel(productRepository, saleRepository, authRepository)
    }

    @Test
    fun `calculo del carrito genera subtotal e IVA correctamente`() = runTest {
        // Dado un carrito con productos (usando IDs distintos para evitar colisiones)
        val product1 = Product(id = "p1", price = 100.0, stock = 10)
        val product2 = Product(id = "p2", price = 50.0, stock = 10)
        
        // Simular agregar al carrito
        viewModel.addToCart(product1)
        viewModel.addToCart(product1) // Cantidad 2: 200.0
        viewModel.addToCart(product2) // Cantidad 1: 50.0
        
        // REQUERIMIENTO:
        // Total: Suma de precios = 200 + 50 = 250.0
        // IVA: 16% del Total = 250 * 0.16 = 40.0
        // Subtotal: Total - IVA = 250 - 40 = 210.0

        val totalValue = viewModel.total.first()
        val ivaValue = viewModel.iva.first()
        val subtotalValue = viewModel.subtotal.first()

        assertEquals(250.0, totalValue, 0.01)
        assertEquals(40.0, ivaValue, 0.01)
        assertEquals(210.0, subtotalValue, 0.01)
    }

    @Test
    fun `addToCart incrementa cantidad si el producto ya existe`() = runTest {
        val product = Product(id = "1", name = "Test", price = 10.0, stock = 5)
        
        viewModel.addToCart(product)
        viewModel.addToCart(product)

        val items = viewModel.cartItems.value
        assertEquals(1, items.size)
        assertEquals(2, items[0].quantity)
    }

    @Test
    fun `addToCart no agrega si no hay stock`() = runTest {
        val product = Product(id = "1", name = "No Stock", price = 10.0, stock = 0)
        
        viewModel.addToCart(product)

        val items = viewModel.cartItems.value
        assertEquals(0, items.size)
        assertEquals("Producto No Stock sin stock", viewModel.errorMessage.value)
    }
}
