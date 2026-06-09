package com.example.pointofsale.viewmodel.launch


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pointofsale.model.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val userLevel: String) : AuthState()
}

class LaunchViewModel(private val authRepository: AuthRepository = AuthRepository()) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkUserSession()
    }

    private fun checkUserSession() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser()
            if (currentUser != null) {
                val userProfile = authRepository.getUserProfile(currentUser.uid)
                if (userProfile != null && userProfile.isActive) {
                    _authState.value = AuthState.Authenticated(userProfile.userLevel)
                } else {
                    _authState.value = AuthState.Unauthenticated
                }
            } else {
                _authState.value = AuthState.Unauthenticated
            }
        }
    }
}
