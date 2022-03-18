package com.example.data.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.example.data.database.model.MessageEntity

@Dao
interface MessagesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(messages: List<MessageEntity>)

    @Query("SELECT * FROM messages_table")
    fun getAllMessagesPaging(): PagingSource<Int, MessageEntity>

    @Query("DELETE FROM messages_table")
    suspend fun deleteAllMessages()
}
