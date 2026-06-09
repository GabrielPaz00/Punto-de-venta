package com.example.pointofsale.model.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.pointofsale.model.entities.User
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    // Retorna el usuario actual de Firebase Auth
    fun getCurrentUser() = auth.currentUser

    suspend fun getUserProfile(uid: String): User? {
        return try {
            val document = firestore.collection("users").document(uid).get().await()
            document.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    // Inicia sesión y luego busca los datos extendidos del usuario en Firestore
    suspend fun login(email: String, contrasena: String): Result<User> {
        return try {
            // 1. Autenticar en Firebase Auth
            val authResult = auth.signInWithEmailAndPassword(email, contrasena).await()
            val uid = authResult.user?.uid ?: throw Exception("Usuario no encontrado")

            // 2. Obtener el rol y datos desde Firestore
            val document = firestore.collection("users").document(uid).get().await()
            val userProfile = document.toObject(User::class.java)
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
}