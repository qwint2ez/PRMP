package com.example.calculator.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.Query

class HistoryRepository {
    private val db = FirebaseFirestore.getInstance()
    private val historyCollection = db.collection("history")

    suspend fun addEntry(entry: HistoryEntry) {
        historyCollection.add(entry).await()
    }

    suspend fun loadHistory(): List<HistoryEntry> {
        return try {
            val snapshot = historyCollection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
            snapshot.toObjects(HistoryEntry::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}