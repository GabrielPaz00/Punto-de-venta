package com.example.pointofsale.viewmodel.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pointofsale.model.entities.User
import com.example.pointofsale.model.repository.AuthRepository
import com.example.pointofsale.model.repository.UserRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class UsersViewModel(
    private val userRepository: UserRepository = UserRepository(),
    private val authRepository: AuthRepository = AuthRepository.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(UsersUiState())
    val uiState: StateFlow<UsersUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Singleton info for current user
    val currentUserProfile: StateFlow<User?> = authRepository.userProfile

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            combine(
                userRepository.getUsersFlow(),
                _searchQuery
            ) { users, query ->
                val trimmedQuery = query.trim()
                if (trimmedQuery.isEmpty()) users
                else users.filter { user ->
                    user.username.contains(trimmedQuery, ignoreCase = true) || 
                    user.email.contains(trimmedQuery, ignoreCase = true) 
                }
            }
            .catch { e ->
                val errorDetail = when (e) {
                    is FirebaseFirestoreException -> {
                        when (e.code) {
                            FirebaseFirestoreException.Code.PERMISSION_DENIED -> 
                                "Error [PERMISSION_DENIED]: No tienes permisos para leer la lista de usuarios. Asegúrate de tener el rol 'admin' en Firestore y que las reglas de seguridad estén configuradas."
                            FirebaseFirestoreException.Code.UNAVAILABLE -> 
                                "Error [UNAVAILABLE]: El servicio de Firestore no está disponible. Revisa tu conexión a internet."
                            FirebaseFirestoreException.Code.NOT_FOUND ->
                                "Error [NOT_FOUND]: La colección o el documento de usuarios no existe."
                            else -> "Error de Firestore [${e.code}]: ${e.localizedMessage}"
                        }
                    }
                    is FirebaseNetworkException -> "Error de Red: No hay conexión con los servidores de Firebase."
                    else -> "Error inesperado: ${e.localizedMessage ?: e.toString()}"
                }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = errorDetail
                )
            }
            .collect { filteredUsers ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    usersList = filteredUsers,
                    errorMessage = null
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun updateUserRole(user: User, newRole: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val updatedUser = user.copy(userLevel = newRole)
            val result = userRepository.updateUser(updatedUser)
            
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isUpdateSuccess = result.isSuccess,
                errorMessage = result.exceptionOrNull()?.message
            )
        }
    }

    fun resetUpdateStatus() {
        _uiState.value = _uiState.value.copy(isUpdateSuccess = false, errorMessage = null)
    }

    fun createUser(username: String, email: String, password: String, role: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val newUser = User(
                username = username,
                email = email,
                userLevel = role,
                isActive = true
            )
            val result = userRepository.createUser(newUser, password)
            
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isUpdateSuccess = result.isSuccess,
                errorMessage = result.exceptionOrNull()?.message
            )
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = userRepository.deleteUser(user.uid)
            
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isUpdateSuccess = result.isSuccess,
                errorMessage = result.exceptionOrNull()?.message
            )
        }
    }
}
