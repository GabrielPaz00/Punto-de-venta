package com.example.pointofsale.model.repository

import android.util.Log
import com.example.pointofsale.model.entities.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ProductRepository(private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()) {
    private val productsCollection = firestore.collection("products")

    fun getAllProductsFlow(): Flow<List<Product>> {
        return productsCollection
            .orderBy("name", Query.Direction.ASCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Product::class.java)
            }
            .catch { e ->
                Log.e("ProductRepository", "Error en getAllProductsFlow: ${e.message}", e)
                throw e
            }
    }
    
    // Obtiene un producto por su ID
    suspend fun getProductById(productId: String): Result<Product?> {
        return try {
            val snapshot = productsCollection.document(productId).get().await()
            val product = snapshot.toObject(Product::class.java)
            Result.success(product)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Crea un nuevo producto (Firestore genera el ID automáticamente)
    suspend fun createProduct(product: Product): Result<Boolean> {
        return try {
            productsCollection.add(product).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Actualiza un producto existente
    suspend fun updateProduct(product: Product): Result<Boolean> {
        return try {
            if (product.id.isEmpty()) throw Exception("El ID del producto está vacío")
            productsCollection.document(product.id).set(product).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Actualiza solo el stock de un producto
    suspend fun updateStock(productId: String, newStock: Int): Result<Boolean> {
        return try {
            if (productId.isEmpty()) throw Exception("El ID del producto está vacío")
            productsCollection.document(productId).update("stock", newStock).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    // Elimina un producto
    suspend fun deleteProduct(productId: String): Result<Boolean> {
        return try {
            productsCollection.document(productId).delete().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Busca productos por nombre (coincidencia parcial)
    fun searchProductsByName(query: String): Flow<List<Product>> {
        return productsCollection
            .whereGreaterThanOrEqualTo("name", query)
            .whereLessThanOrEqualTo("name", query + "\uf8ff")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Product::class.java)
            }
            .catch { e ->
                Log.e("ProductRepository", "Error en searchProductsByName: ${e.message}", e)
                throw e
            }
    }
}