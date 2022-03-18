package com.example.data.usecases

import com.example.data.database.model.MessageEntity
import kotlinx.coroutines.flow.Flow

interface GetMessagesUseCase {
    operator fun invoke(last: String?): Flow<List<MessageEntity>>
}