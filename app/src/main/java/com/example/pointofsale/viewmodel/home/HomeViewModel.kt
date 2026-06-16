package com.example.pointofsale.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pointofsale.model.entities.User
import com.example.pointofsale.model.repository.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    private val authRepository: AuthRepository = AuthRepository.getInstance()
) : ViewModel() {
    
    val userState: StateFlow<User> = authRepository.userProfile
        .map { it ?: User() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = User()
        )

    init {
        // El perfil se carga automáticamente en AuthRepository.init o vía refreshProfile
    }

    fun fetchUserProfile() {
        authRepository.refreshProfile()
    }

    fun clearState() {
        // En una arquitectura reactiva, el "clear" suele venir del logout en el repositorio
    }
}
