package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedItemDao {
    @Query("SELECT * FROM saved_items ORDER BY savedAt DESC")
    fun getAllSavedItems(): Flow<List<SavedItem>>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_items WHERE id = :id LIMIT 1)")
    fun isItemSaved(id: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: SavedItem)

    @Delete
    suspend fun deleteItem(item: SavedItem)

    @Query("DELETE FROM saved_items WHERE id = :id")
    suspend fun deleteItemById(id: String)
}

@Dao
interface SavedArticleDao {
    @Query("SELECT * FROM saved_articles ORDER BY savedAt DESC")
    fun getAllSavedArticles(): Flow<List<SavedArticle>>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_articles WHERE id = :id LIMIT 1)")
    fun isArticleSaved(id: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: SavedArticle)

    @Delete
    suspend fun deleteArticle(article: SavedArticle)

    @Query("DELETE FROM saved_articles WHERE id = :id")
    suspend fun deleteArticleById(id: String)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("DELETE FROM chat_messages")
    suspend fun clearHistory()
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM space_notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationItem>>

    @Query("SELECT COUNT(*) FROM space_notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationItem)

    @Query("UPDATE space_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE space_notifications SET isRead = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM space_notifications WHERE id = :id")
    suspend fun deleteNotification(id: Long)

    @Query("DELETE FROM space_notifications")
    suspend fun clearAll()
}
