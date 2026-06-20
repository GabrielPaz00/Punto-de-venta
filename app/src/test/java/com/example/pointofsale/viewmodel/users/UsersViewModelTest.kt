package com.example.pointofsale.viewmodel.users

import com.example.pointofsale.model.entities.User
import com.example.pointofsale.model.repository.AuthRepository
import com.example.pointofsale.model.repository.UserRepository
import com.example.pointofsale.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UsersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var userRepository: UserRepository
    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: UsersViewModel

    @Before
    fun setup() {
        userRepository = mockk()
        authRepository = mockk()

        // Mock default flows
        every { userRepository.getUsersFlow() } returns flowOf(emptyList())
        every { authRepository.userProfile } returns MutableStateFlow(null)

        viewModel = UsersViewModel(userRepository, authRepository)
    }

    @Test
    fun `createUser llama al repositorio con los datos correctos`() = runTest {
        // Dado
        val username = "Juan Perez"
        val email = "juan@empresa.com"
        val password = "123"
        val role = "Usuario Estándar"
        
        coEvery { userRepository.createUser(any(), password) } returns Result.success(true)

        // Cuando
        viewModel.createUser(username, email, password, role)

        // Entonces
        coVerify(exactly = 1) { 
            userRepository.createUser(match { 
                it.username == username && it.email == email && it.userLevel == role 
            }, password) 
        }
        assertTrue(viewModel.uiState.value.isUpdateSuccess)
    }

    @Test
    fun `updateUserRole llama al repositorio para actualizar el nivel del usuario`() = runTest {
        // Dado
        val user = User(uid = "user123", username = "Juan", userLevel = "Usuario")
        val newRole = "Admin"
        coEvery { userRepository.updateUser(any()) } returns Result.success(true)

        // Cuando
        viewModel.updateUserRole(user, newRole)

        // Entonces
        coVerify(exactly = 1) { 
            userRepository.updateUser(match { it.uid == user.uid && it.userLevel == newRole }) 
        }
        assertTrue(viewModel.uiState.value.isUpdateSuccess)
    }
}
