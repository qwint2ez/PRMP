package com.example.calculator.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ThemeRepository {
    private val db = FirebaseFirestore.getInstance()
    private val settingsCollection = db.collection("settings")
    private val documentId = "theme"

    suspend fun loadTheme(): ThemeSettings? {
        return try {
            val document = settingsCollection.document(documentId).get().await()
            document.toObject(ThemeSettings::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveTheme(settings: ThemeSettings) {
        settingsCollection.document(documentId).set(settings).await()
    }
}