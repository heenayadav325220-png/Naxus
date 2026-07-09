package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        SavedItem::class,
        SavedArticle::class,
        ChatMessage::class,
        NotificationItem::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SpaceDatabase : RoomDatabase() {
    abstract fun savedItemDao(): SavedItemDao
    abstract fun savedArticleDao(): SavedArticleDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: SpaceDatabase? = null

        fun getDatabase(context: Context): SpaceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SpaceDatabase::class.java,
                    "starnexus_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
