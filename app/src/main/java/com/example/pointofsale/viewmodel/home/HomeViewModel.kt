package com.example.pointofsale.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pointofsale.model.repository.AuthRepository
import com.example.pointofsale.model.repository.UserRepository
import com.example.pointofsale.model.entities.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    authRepository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {
    private val firebaseUser = authRepository.getCurrentUser()
    private val _userState = MutableStateFlow(
        User(
            uid = firebaseUser?.uid ?: "",
            username = firebaseUser?.displayName ?: "Usuario",
            email = firebaseUser?.email ?: ""
        )
    )
    val userState = _userState.asStateFlow()

    init {
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        firebaseUser?.uid?.let { uid ->
            viewModelScope.launch {
                userRepository.getUserProfile(uid)?.let { profile ->
                    _userState.value = profile
                }
            }
        }
    }
}
