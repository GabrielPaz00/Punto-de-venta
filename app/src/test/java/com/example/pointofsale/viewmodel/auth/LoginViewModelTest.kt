package com.example.pointofsale.viewmodel.auth

import com.example.pointofsale.model.entities.User
import com.example.pointofsale.model.repository.AuthRepository
import com.example.pointofsale.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        authRepository = mockk()
        viewModel = LoginViewModel(authRepository)
    }

    @Test
    fun `login con credenciales validas actualiza uiState a autenticado`() = runTest {
        // Dado
        val email = "admin@institucional.com"
        val password = "password123"
        val mockUser = User(uid = "uid123", username = "Admin", email = email)
        
        coEvery { authRepository.login(email, password) } returns Result.success(mockUser)

        // Cuando
        viewModel.login(email, password)

        // Entonces
        val state = viewModel.uiState.value
        assertTrue(state.isAuthenticated)
        assertEquals(mockUser, state.userProfile)
        assertEquals(null, state.errorMessage)
    }

    @Test
    fun `login con credenciales invalidas actualiza uiState con error`() = runTest {
        // Dado
        val email = "error@test.com"
        val password = "wrong"
        val exception = Exception("Invalid credentials")
        
        coEvery { authRepository.login(email, password) } returns Result.failure(exception)

        // Cuando
        viewModel.login(email, password)

        // Entonces
        val state = viewModel.uiState.value
        assertTrue(!state.isAuthenticated)
        assertEquals("Invalid credentials", state.errorMessage)
    }

    @Test
    fun `login con campos vacios muestra error sin llamar al repositorio`() = runTest {
        // Cuando
        viewModel.login("", "")

        // Entonces
        val state = viewModel.uiState.value
        assertEquals("Por favor, complete todos los campos", state.errorMessage)
    }
}
