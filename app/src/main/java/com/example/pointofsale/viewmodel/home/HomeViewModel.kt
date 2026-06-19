package com.example.pointofsale.viewmodel.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pointofsale.model.entities.User
import com.example.pointofsale.model.repository.AuthRepository
import com.example.pointofsale.model.repository.SaleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeSummary(
    val totalSales: Double = 0.0,
    val ordersCount: Int = 0
)

class HomeViewModel(
    private val authRepository: AuthRepository = AuthRepository.getInstance(),
    private val saleRepository: SaleRepository = SaleRepository()
) : ViewModel() {
    
    val userState: StateFlow<User> = authRepository.userProfile
        .map { it ?: User() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = User()
        )

    private val _summaryState = MutableStateFlow(HomeSummary())
    val summaryState: StateFlow<HomeSummary> = _summaryState.asStateFlow()

    init {
        // El perfil se carga automáticamente en AuthRepository.init o vía refreshProfile
        fetchTodaysSummary()
    }

    fun fetchTodaysSummary() {
        viewModelScope.launch {
            saleRepository.getTodaysSales().onSuccess { sales ->
                val total = sales.sumOf { it.total }
                _summaryState.value = HomeSummary(
                    totalSales = total,
                    ordersCount = sales.size
                )
            }
        }
    }

    fun fetchUserProfile() {
        authRepository.refreshProfile()
    }

    fun clearState() {
        // En una arquitectura reactiva, el "clear" suele venir del logout en el repositorio
    }
}
