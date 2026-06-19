package com.example.pointofsale.model.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.example.pointofsale.model.entities.User
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val usersCollection = firestore.collection("users")

    suspend fun createUser(user: User, password: String): Result<Boolean> {
        return try {
            // 1. Crear usuario en Auth usando una instancia secundaria para no cerrar la sesión actual
            val context = FirebaseApp.getInstance().applicationContext
            val options = FirebaseApp.getInstance().options
            
            val secondaryApp = try {
                FirebaseApp.initializeApp(context, options, "SecondaryApp")
            } catch (e: Exception) {
                FirebaseApp.getInstance("SecondaryApp")
            }
            
            val secondaryAuth = FirebaseAuth.getInstance(secondaryApp)
            
            val authResult = secondaryAuth.createUserWithEmailAndPassword(user.email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("No se pudo obtener el UID del nuevo usuario")
            
            // Cerrar la instancia secundaria inmediatamente después de crear el usuario
            secondaryApp.delete()
            
            // 2. Guardar datos en Firestore con el UID obtenido usando la instancia principal
            val userWithUid = user.copy(uid = uid)
            usersCollection.document(uid).set(userWithUid).await()
            Result.success(true)
        } catch (e: Exception) {
            // Asegurarse de limpiar si hay error
            try { FirebaseApp.getInstance("SecondaryApp").delete() } catch (_: Exception) {}
            Result.failure(e)
        }
    }

    // Flujo en tiempo real de todos los usuarios
    fun getUsersFlow(): Flow<List<User>> {
        return usersCollection
            .orderBy("username", Query.Direction.ASCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(User::class.java)
            }
            .catch { e ->
                Log.e("UserRepository", "Error en getUsersFlow: ${e.message}", e)
                throw e
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

    suspend fun deleteUser(uid: String): Result<Boolean> {
        return try {
            usersCollection.document(uid).delete().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
