package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_items")
data class SavedItem(
    @PrimaryKey val id: String,
    val category: String, // "planet", "star", "galaxy", "blackhole", "nebula", "mission", "satellite", "astronaut"
    val name: String,
    val description: String,
    val detailText: String,
    val imageResName: String, // e.g., "ic_mars" or general key
    val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_articles")
data class SavedArticle(
    @PrimaryKey val id: String,
    val title: String,
    val summary: String,
    val publishedDate: String,
    val source: String,
    val content: String,
    val imageUrl: String,
    val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val role: String, // "user" or "model"
    val text: String,
    val imageUriString: String? = null, // for multi-modal image explanations
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "space_notifications")
data class NotificationItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val body: String,
    val category: String, // "news", "meteor", "iss", "moon"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
