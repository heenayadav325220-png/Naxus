package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.ChatMessage
import com.example.data.database.NotificationItem
import com.example.data.database.SavedArticle
import com.example.data.database.SavedItem
import com.example.data.model.DiscoveryItem
import com.example.data.model.NewsArticle
import com.example.data.repository.SpaceRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SpaceViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SpaceRepository(application)

    // --- Navigation & Core App State ---
    private val _currentScreen = MutableStateFlow("splash") // "splash", "login", "main"
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    private val _activeTab = MutableStateFlow("home") // "home", "sky", "discover", "ai", "profile"
    val activeTab: StateFlow<String> = _activeTab.asStateFlow()

    // --- Search & Filters ---
    private val _globalSearchQuery = MutableStateFlow("")
    val globalSearchQuery: StateFlow<String> = _globalSearchQuery.asStateFlow()

    private val _discoverCategoryFilter = MutableStateFlow("all")
    val discoverCategoryFilter: StateFlow<String> = _discoverCategoryFilter.asStateFlow()

    // --- Detailed Inspector States (Modals / Full Detail overlays) ---
    private val _selectedDiscoveryItem = MutableStateFlow<DiscoveryItem?>(null)
    val selectedDiscoveryItem: StateFlow<DiscoveryItem?> = _selectedDiscoveryItem.asStateFlow()

    private val _selectedArticle = MutableStateFlow<NewsArticle?>(null)
    val selectedArticle: StateFlow<NewsArticle?> = _selectedArticle.asStateFlow()

    // --- Favorites & Saved lists (Reactive flows) ---
    val savedItems: StateFlow<List<SavedItem>> = repository.savedItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedArticles: StateFlow<List<SavedArticle>> = repository.savedArticles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- AI Chatbot States ---
    val chatMessages: StateFlow<List<ChatMessage>> = repository.chatMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _chatInputText = MutableStateFlow("")
    val chatInputText: StateFlow<String> = _chatInputText.asStateFlow()

    private val _chatSelectedImage = MutableStateFlow<Bitmap?>(null)
    val chatSelectedImage: StateFlow<Bitmap?> = _chatSelectedImage.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    // --- Notifications Hub ---
    val notifications: StateFlow<List<NotificationItem>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationsCount: StateFlow<Int> = repository.unreadNotificationsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // --- Settings & Customization Preferences ---
    private val _isDarkMode = MutableStateFlow(true) // Defaults to true (premium dark space vibe)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _languageSetting = MutableStateFlow("English") // "English", "Español", "Français", "हिन्दी"
    val languageSetting: StateFlow<String> = _languageSetting.asStateFlow()

    // --- Firebase Authentication States ---
    val firebaseUser: StateFlow<FirebaseUser?> = repository.userState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        // Populate initial sample space alerts
        viewModelScope.launch {
            repository.populateSampleNotifications()
        }
    }

    // --- Actions & Methods ---

    fun setScreen(screen: String) {
        _currentScreen.value = screen
    }

    fun setActiveTab(tab: String) {
        _activeTab.value = tab
    }

    fun setGlobalSearchQuery(query: String) {
        _globalSearchQuery.value = query
    }

    fun setDiscoverCategory(category: String) {
        _discoverCategoryFilter.value = category
    }

    fun inspectDiscoveryItem(item: DiscoveryItem?) {
        _selectedDiscoveryItem.value = item
    }

    fun inspectArticle(article: NewsArticle?) {
        _selectedArticle.value = article
    }

    // --- Bookmark operations ---

    fun toggleSaveItem(item: DiscoveryItem) {
        viewModelScope.launch {
            val isCurrentlySaved = savedItems.value.any { it.id == item.id }
            if (isCurrentlySaved) {
                repository.unsaveItemById(item.id)
            } else {
                repository.saveItem(item)
            }
        }
    }

    fun toggleSaveArticle(article: NewsArticle) {
        viewModelScope.launch {
            val isCurrentlySaved = savedArticles.value.any { it.id == article.id }
            if (isCurrentlySaved) {
                repository.unsaveArticleById(article.id)
            } else {
                repository.saveArticle(article)
            }
        }
    }

    fun isItemSaved(id: String): Flow<Boolean> {
        return repository.isItemSavedFlow(id)
    }

    fun isArticleSaved(id: String): Flow<Boolean> {
        return repository.isArticleSavedFlow(id)
    }

    // --- AI Assistant Chat Operations ---

    fun setChatInputText(text: String) {
        _chatInputText.value = text
    }

    fun setChatSelectedImage(bitmap: Bitmap?) {
        _chatSelectedImage.value = bitmap
    }

    fun sendChatMessage() {
        val prompt = _chatInputText.value.trim()
        if (prompt.isEmpty() && _chatSelectedImage.value == null) return

        val image = _chatSelectedImage.value
        _chatInputText.value = ""
        _chatSelectedImage.value = null
        _isAiThinking.value = true

        viewModelScope.launch {
            try {
                val currentHistory = chatMessages.value
                repository.sendMessageToAi(prompt, image, currentHistory)
            } catch (e: Exception) {
                // Handled gracefully inside repository fallback
            } finally {
                _isAiThinking.value = false
            }
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            repository.clearChatHistory()
        }
    }

    // --- Notification Hub Operations ---

    fun markNotificationAsRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    fun deleteNotification(id: Long) {
        viewModelScope.launch {
            repository.deleteNotificationById(id)
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            repository.clearAllNotifications()
        }
    }

    // --- Settings / Personalization Toggles ---

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }

    fun setLanguageSetting(lang: String) {
        _languageSetting.value = lang
    }

    // --- Firebase Authentication Methods ---

    fun isFirebaseAvailable(): Boolean = repository.isFirebaseAvailable()

    fun loginAnonymously(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = repository.signInAnonymously()
            if (user != null) {
                // Restore their cloud favorites if they had any
                repository.restoreFavoritesFromCloud(user.uid)
                onComplete(true)
            } else {
                onComplete(false)
            }
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            repository.signOut()
            setScreen("login")
        }
    }
}
