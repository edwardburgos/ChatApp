package com.example.data.repository

import androidx.paging.PagingData
import com.example.data.database.model.MessageEntity
import kotlinx.coroutines.flow.Flow

interface MessagesRepository {
    fun getMessages(last: String?): Flow<List<MessageEntity>>
    fun getPager(last: String?): Flow<PagingData<MessageEntity>>
}