package com.example.pointofsale.model.repository

import com.example.pointofsale.model.entities.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthRepository private constructor(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val userRepository: UserRepository = UserRepository()
) {
    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile.asStateFlow()

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    init {
        // Listen for auth state changes
        auth.addAuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser == null) {
                _userProfile.value = null
            } else {
                refreshProfile()
            }
        }
    }

    fun refreshProfile() {
        val uid = auth.currentUser?.uid ?: return
        repositoryScope.launch {
            try {
                val profile = userRepository.getUserProfile(uid)
                _userProfile.value = profile ?: User(
                    uid = uid,
                    username = auth.currentUser?.displayName ?: "Usuario",
                    email = auth.currentUser?.email ?: ""
                )
            } catch (e: Exception) {
                // Keep previous state or set error
            }
        }
    }

    fun getCurrentUser() = auth.currentUser

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            // 1. Autenticar en Firebase Auth
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Usuario no encontrado")

            // 2. Obtener el perfil del usuario usando UserRepository
            val userProfile = userRepository.getUserProfile(uid)
                ?: throw Exception("Perfil de usuario no encontrado en la base de datos")

            if (!userProfile.isActive) {
                auth.signOut()
                throw Exception("Este usuario está desactivado")
            }

            _userProfile.value = userProfile
            Result.success(userProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
        _userProfile.value = null
    }

    suspend fun getCurrentUserProfile(): User? {
        val firebaseUser = auth.currentUser ?: return null

        // Try to get the complete profile from Firestore
        val profile = userRepository.getUserProfile(firebaseUser.uid)

        val finalProfile = profile ?: User(
            uid = firebaseUser.uid,
            username = firebaseUser.displayName ?: "Usuario",
            email = firebaseUser.email ?: ""
        )
        _userProfile.value = finalProfile
        return finalProfile
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthRepository? = null

        fun getInstance(): AuthRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthRepository().also { INSTANCE = it }
            }
        }
    }
}
