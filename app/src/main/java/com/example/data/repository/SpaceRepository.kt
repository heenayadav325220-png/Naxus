package com.example.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.data.api.GeminiClient
import com.example.data.database.*
import com.example.data.model.DiscoveryItem
import com.example.data.model.NewsArticle
import com.example.data.model.SpaceData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SpaceRepository(context: Context) {
    private val TAG = "SpaceRepository"
    private val database = SpaceDatabase.getDatabase(context)

    private val savedItemDao = database.savedItemDao()
    private val savedArticleDao = database.savedArticleDao()
    private val chatMessageDao = database.chatMessageDao()
    private val notificationDao = database.notificationDao()

    // --- Firebase Authentication & State ---
    private var firebaseAuth: FirebaseAuth? = null
    private var firestore: FirebaseFirestore? = null

    private val _userState = MutableStateFlow<FirebaseUser?>(null)
    val userState: StateFlow<FirebaseUser?> = _userState

    init {
        setupFirebase()
    }

    private fun setupFirebase() {
        try {
            firebaseAuth = FirebaseAuth.getInstance()
            firestore = FirebaseFirestore.getInstance()
            _userState.value = firebaseAuth?.currentUser
            
            firebaseAuth?.addAuthStateListener { auth ->
                _userState.value = auth.currentUser
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firebase not fully initialized. Operating in local-only / demo mode. Details: ${e.message}")
        }
    }

    // --- Authentication Actions ---
    
    fun isFirebaseAvailable(): Boolean = firebaseAuth != null

    suspend fun signInAnonymously(): FirebaseUser? = withContext(Dispatchers.IO) {
        val auth = firebaseAuth ?: return@withContext null
        try {
            val result = auth.signInAnonymously().await()
            result.user
        } catch (e: Exception) {
            Log.e(TAG, "Firebase Anonymous Sign-In failed: ${e.message}")
            null
        }
    }

    suspend fun signOut() = withContext(Dispatchers.IO) {
        val auth = firebaseAuth
        if (auth != null) {
            try {
                auth.signOut()
                _userState.value = null
            } catch (e: Exception) {
                Log.e(TAG, "Firebase Sign-Out failed: ${e.message}")
            }
        }
    }

    // --- Firestore Data Persistence (Cloud Backup for Favorites) ---

    suspend fun backupFavoritesToCloud(userId: String, items: List<SavedItem>) = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext
        try {
            val userRef = db.collection("users").document(userId).collection("favorites")
            for (item in items) {
                val data = mapOf(
                    "id" to item.id,
                    "category" to item.category,
                    "name" to item.name,
                    "description" to item.description,
                    "detailText" to item.detailText,
                    "imageResName" to item.imageResName,
                    "savedAt" to item.savedAt
                )
                userRef.document(item.id).set(data).await()
            }
            Log.d(TAG, "Favorites successfully backed up to Firestore.")
        } catch (e: Exception) {
            Log.e(TAG, "Firestore backup failed: ${e.message}")
        }
    }

    suspend fun restoreFavoritesFromCloud(userId: String) = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext
        try {
            val snapshots = db.collection("users").document(userId).collection("favorites").get().await()
            for (doc in snapshots.documents) {
                val id = doc.getString("id") ?: continue
                val item = SavedItem(
                    id = id,
                    category = doc.getString("category") ?: "planet",
                    name = doc.getString("name") ?: "",
                    description = doc.getString("description") ?: "",
                    detailText = doc.getString("detailText") ?: "",
                    imageResName = doc.getString("imageResName") ?: "",
                    savedAt = doc.getLong("savedAt") ?: System.currentTimeMillis()
                )
                savedItemDao.insertItem(item)
            }
            Log.d(TAG, "Favorites successfully restored from Firestore.")
        } catch (e: Exception) {
            Log.e(TAG, "Firestore restore failed: ${e.message}")
        }
    }

    // --- Local Discover & Articles Queries (Search, Filter, Bookmarks) ---

    fun getDiscoveries(): List<DiscoveryItem> = SpaceData.discoveries

    fun getNewsArticles(): List<NewsArticle> = SpaceData.articles

    fun searchDiscoveries(query: String, categoryFilter: String = "all"): List<DiscoveryItem> {
        val lowerQuery = query.lowercase().trim()
        return SpaceData.discoveries.filter { item ->
            val matchesQuery = lowerQuery.isEmpty() || 
                    item.name.lowercase().contains(lowerQuery) || 
                    item.shortDescription.lowercase().contains(lowerQuery) ||
                    item.detailedDescription.lowercase().contains(lowerQuery)
            
            val matchesCategory = categoryFilter == "all" || item.category == categoryFilter
            
            matchesQuery && matchesCategory
        }
    }

    // --- Bookmarked / Saved Items (Room Flow) ---

    val savedItems: Flow<List<SavedItem>> = savedItemDao.getAllSavedItems()

    fun isItemSavedFlow(id: String): Flow<Boolean> = savedItemDao.isItemSaved(id)

    suspend fun saveItem(item: DiscoveryItem) = withContext(Dispatchers.IO) {
        savedItemDao.insertItem(item.toSavedItem())
        // Proactively back up if logged in
        _userState.value?.uid?.let { uid ->
            backupFavoritesToCloud(uid, listOf(item.toSavedItem()))
        }
    }

    suspend fun unsaveItemById(id: String) = withContext(Dispatchers.IO) {
        savedItemDao.deleteItemById(id)
        // If logged in, optionally delete from Firestore
        val uid = _userState.value?.uid
        val db = firestore
        if (uid != null && db != null) {
            try {
                db.collection("users").document(uid).collection("favorites").document(id).delete()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to delete item $id from Firestore: ${e.message}")
            }
        }
    }

    // --- Bookmarked / Saved Articles (Room Flow) ---

    val savedArticles: Flow<List<SavedArticle>> = savedArticleDao.getAllSavedArticles()

    fun isArticleSavedFlow(id: String): Flow<Boolean> = savedArticleDao.isArticleSaved(id)

    suspend fun saveArticle(article: NewsArticle) = withContext(Dispatchers.IO) {
        savedArticleDao.insertArticle(article.toSavedArticle())
    }

    suspend fun unsaveArticleById(id: String) = withContext(Dispatchers.IO) {
        savedArticleDao.deleteArticleById(id)
    }

    // --- Chat Messages (Room Flow & Gemini REST Integration) ---

    val chatMessages: Flow<List<ChatMessage>> = chatMessageDao.getAllMessages()

    suspend fun sendMessageToAi(prompt: String, image: Bitmap?, currentHistory: List<ChatMessage>): String {
        // 1. Save user message locally
        val userMsg = ChatMessage(role = "user", text = prompt)
        withContext(Dispatchers.IO) {
            chatMessageDao.insertMessage(userMsg)
        }

        // 2. Query Gemini API
        val replyText = GeminiClient.chat(prompt, image, currentHistory + userMsg)

        // 3. Save AI reply locally
        val aiMsg = ChatMessage(role = "model", text = replyText)
        withContext(Dispatchers.IO) {
            chatMessageDao.insertMessage(aiMsg)
        }

        return replyText
    }

    suspend fun clearChatHistory() = withContext(Dispatchers.IO) {
        chatMessageDao.clearHistory()
    }

    // --- Notifications Hub ---

    val notifications: Flow<List<NotificationItem>> = notificationDao.getAllNotifications()
    val unreadNotificationsCount: Flow<Int> = notificationDao.getUnreadCount()

    suspend fun addNotification(title: String, body: String, category: String) = withContext(Dispatchers.IO) {
        val notification = NotificationItem(title = title, body = body, category = category)
        notificationDao.insertNotification(notification)
    }

    suspend fun markNotificationAsRead(id: Long) = withContext(Dispatchers.IO) {
        notificationDao.markAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() = withContext(Dispatchers.IO) {
        notificationDao.markAllAsRead()
    }

    suspend fun deleteNotificationById(id: Long) = withContext(Dispatchers.IO) {
        notificationDao.deleteNotification(id)
    }

    suspend fun clearAllNotifications() = withContext(Dispatchers.IO) {
        notificationDao.clearAll()
    }

    /**
     * Pre-populates some initial notifications so the notification hub is populated with events.
     */
    suspend fun populateSampleNotifications() = withContext(Dispatchers.IO) {
        // Only populate if list is empty
        val list = mutableListOf<NotificationItem>()
        notificationDao.insertNotification(
            NotificationItem(
                title = "☄️ Perseid Meteor Shower Tonight",
                body = "The annual Perseids reach their solar peak tonight! View up to 100 meteors per hour facing North-East after midnight.",
                category = "meteor"
            )
        )
        notificationDao.insertNotification(
            NotificationItem(
                title = "📡 ISS Visible Over Your Location",
                body = "The International Space Station will pass overhead tonight at 9:42 PM. Visible for 6 mins, Max altitude: 75°.",
                category = "iss"
            )
        )
        notificationDao.insertNotification(
            NotificationItem(
                title = "🌕 Full Strawberry Moon Event",
                body = "June's Full Moon rises tonight at 8:14 PM, appearing near the red supergiant Antares in Scorpio constellation.",
                category = "moon"
            )
        )
    }
}
