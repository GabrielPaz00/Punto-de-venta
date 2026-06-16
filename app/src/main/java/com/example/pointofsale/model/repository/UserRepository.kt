package com.example.pointofsale.model.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.example.pointofsale.model.entities.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val usersCollection = firestore.collection("users")

    // Flujo en tiempo real de todos los usuarios
    fun getUsersFlow(): Flow<List<User>> {
        return usersCollection
            .orderBy("nombreCompleto")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(User::class.java)
            }
    }

    // Guardar los datos de un usuario en Firestore después de crear su auth
    suspend fun saveUserData(user: User): Result<Boolean> {
        return try {
            if (user.uid.isEmpty()) throw Exception("El UID es obligatorio")

            // Usamos el UID de Firebase Auth como ID del documento en Firestore
            usersCollection.document(user.uid).set(user).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Actualiza datos del usuario (como cambiar su rol o desactivarlo)
    suspend fun updateUser(user: User): Result<Boolean> {
        return try {
            usersCollection.document(user.uid).set(user).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener perfil de usuario por UID
    suspend fun getUserProfile(uid: String): User? {
        return try {
            val document = usersCollection.document(uid).get().await()
            document.toObject(User::class.java)?.copy(uid = uid)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUsername(uid: String, newUsername: String): Result<Boolean> {
        return try {
            usersCollection.document(uid).update("username", newUsername).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
