package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.database.dao.MessagesDao
import com.example.data.database.dao.RemoteKeysDao
import com.example.data.database.model.MessageEntity
import com.example.data.database.model.RemoteKeysEntity

@Database(entities = [MessageEntity::class, RemoteKeysEntity::class], version = 2, exportSchema = false)
abstract class MessagesDatabase: RoomDatabase() {

    abstract val messagesDao: MessagesDao
    abstract val remoteKeysDao: RemoteKeysDao

    companion object {

        @Volatile
        private var INSTANCE: MessagesDatabase? = null

        fun getInstance(context: Context): MessagesDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MessagesDatabase::class.java,
                        "characters_database",
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}