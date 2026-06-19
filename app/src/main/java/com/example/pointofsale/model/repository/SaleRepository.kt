package com.example.pointofsale.model.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.example.pointofsale.model.entities.Sale
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date

class SaleRepository private constructor(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val salesCollection = firestore.collection("sales")
    private val productsCollection = firestore.collection("products")

    private val _saleEvents = MutableSharedFlow<Unit>()
    val saleEvents = _saleEvents.asSharedFlow()

    suspend fun registerSale(sale: Sale): Result<Boolean> {
        return try {
            val batch = firestore.batch()
            val newSaleRef = salesCollection.document()
            batch.set(newSaleRef, sale)

            sale.products.forEach { item ->
                val productRef = productsCollection.document(item.productId)

                batch.update(productRef, "stock", FieldValue.increment(-item.quantity.toLong()))
            }

            batch.commit().await()
            _saleEvents.emit(Unit)

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTodaysSales(): Result<List<Sale>> {
        return try {
            val startOfDay = getStartOfDay()
            // Usamos 'saleDate' para coincidir con la entidad Sale
            val snapshot = salesCollection
                .whereGreaterThanOrEqualTo("saleDate", startOfDay)
                .get()
                .await()
            Result.success(snapshot.toObjects(Sale::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getStartOfDay(): Date {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.time
    }

    companion object {
        @Volatile
        private var INSTANCE: SaleRepository? = null

        fun getInstance(): SaleRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SaleRepository().also { INSTANCE = it }
            }
        }
    }
}
