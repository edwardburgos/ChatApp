package com.example.data.usecases

import com.example.data.database.model.MessageEntity
import com.example.data.repository.MessagesRepositoryImpl
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMessagesUseCaseImpl @Inject constructor(private val messagesRepository: MessagesRepositoryImpl) :
    GetMessagesUseCase {
    override fun invoke(last: String?): Flow<List<MessageEntity>> = messagesRepository.getMessages(last)
}