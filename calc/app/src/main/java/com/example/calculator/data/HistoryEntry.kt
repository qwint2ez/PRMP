package com.example.calculator.data

data class HistoryEntry(
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)