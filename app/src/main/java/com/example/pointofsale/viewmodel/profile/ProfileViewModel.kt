package com.example.pointofsale.viewmodel.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pointofsale.model.entities.User
import com.example.pointofsale.model.repository.AuthRepository
import com.example.pointofsale.model.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository.getInstance(),
    private  val userRepository: UserRepository = UserRepository()
) : ViewModel() {


    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()
    private val _userState = MutableStateFlow(User())
    val userState = _userState.asStateFlow()

    init {
        loadUserProfile()
        observeGlobalUserProfile()
    }

    private fun observeGlobalUserProfile() {
        viewModelScope.launch {
            authRepository.userProfile.collect { user ->
                user?.let {
                    _userState.value = it
                    if (!_uiState.value.isEditing) {
                        _uiState.value = _uiState.value.copy(name = it.username)
                    }
                }
            }
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                authRepository.getCurrentUserProfile()?.let { user ->
                    _userState.value = user
                    _uiState.value = _uiState.value.copy(
                        name = user.username,
                        isLoading = false
                    )
                } ?: run {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "No se pudo cargar el perfil"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error al cargar perfil"
                )
            }
        }
    }
    fun onNameChange(newName: String) {
        _uiState.value = _uiState.value.copy(name = newName)
    }
    fun updateUserProfile() {
        val currentUser = _userState.value
        if (currentUser.uid.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "ID de usuario no válido")
            return
        }

        viewModelScope.launch {
            val newUsername = _uiState.value.name
            if (newUsername.isBlank()) {
                _uiState.value = _uiState.value.copy(errorMessage = "El nombre no puede estar vacío")
                return@launch
            }

            _uiState.value = _uiState.value.copy(errorMessage = null, isLoading = true)
            val result = userRepository.updateUsername(currentUser.uid, newUsername)
            if (result.isSuccess) {
                // Actualizar el estado global en el repositorio
                authRepository.refreshProfile()
                _uiState.value = _uiState.value.copy(isEditing = false, isLoading = false)
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Error al actualizar"
                _uiState.value = _uiState.value.copy(errorMessage = errorMsg, isLoading = false)
                Log.e("ProfileViewModel", errorMsg)
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    fun logout() {
        authRepository.logout()
    }

    fun toggleEditMode() {
        val currentState = _uiState.value
        if (currentState.isEditing) {
            _uiState.value = currentState.copy(
                isEditing = false,
                name = _userState.value.username
            )
        } else {
            _uiState.value = currentState.copy(isEditing = true)
        }
    }
}
