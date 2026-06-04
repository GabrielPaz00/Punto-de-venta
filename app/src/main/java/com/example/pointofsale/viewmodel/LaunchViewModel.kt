package com.example.pointofsale.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val userLevel: String) : AuthState()
}

class LaunchViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    init {
        checkUserSession()
    }

    private fun checkUserSession() {
        viewModelScope.launch {
            delay(1200)

            val isLogged = false
            val savedUserLevel = "1"

            if (isLogged) {
                _authState.value = AuthState.Authenticated(savedUserLevel)
            } else {
                _authState.value = AuthState.Unauthenticated
            }
        }
    }
}