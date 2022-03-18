package com.example.data.usecases

import com.example.data.repository.MessagesRepositoryImpl
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMessagesUseCaseImpl @Inject constructor(private val messagesRepository: MessagesRepositoryImpl) :
    GetMessagesUseCase {
    override fun invoke(last: DocumentSnapshot?): Flow<List<DocumentSnapshot>> = messagesRepository.getMessages(last)
}