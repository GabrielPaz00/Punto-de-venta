package com.example.pointofsale.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pointofsale.model.entities.User
import com.example.pointofsale.model.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _userState = MutableStateFlow(User())
    val userState = _userState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            authRepository.getCurrentUserProfile()?.let { user ->
                _userState.value = user
            }
        }
    }

    fun logout() {
        authRepository.logout()
    }
}
