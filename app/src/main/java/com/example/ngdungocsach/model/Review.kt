package com.example.ngdungocsach.model

data class Review(
    val id: String = "",
    val bookId: String = "",
    val uid: String = "",
    val displayName: String = "",
    val rating: Float = 0f,
    val comment: String = "",
    val timestamp: Long = System.currentTimeMillis()
)