package com.example.pointofsale.model.repository

import com.google.firebase.auth.FirebaseAuth
import com.example.pointofsale.model.entities.User
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val userRepository: UserRepository = UserRepository()
) {
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

            Result.success(userProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun getCurrentUserProfile(): User? {
        val firebaseUser = auth.currentUser ?: return null

        // Try to get the complete profile from Firestore
        val profile = userRepository.getUserProfile(firebaseUser.uid)

        // If it exists in Firestore, return it.
        // Otherwise, return a basic User object with Firebase Auth data.
        return profile ?: User(
            uid = firebaseUser.uid,
            username = firebaseUser.displayName ?: "Usuario",
            email = firebaseUser.email ?: ""
        )
    }
}
