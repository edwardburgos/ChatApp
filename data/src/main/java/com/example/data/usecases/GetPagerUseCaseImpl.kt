package com.example.data.usecases

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import com.example.data.database.model.MessageEntity
import com.example.data.repository.MessagesRepositoryImpl
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPagerUseCaseImpl @Inject constructor(private val messagesRepository: MessagesRepositoryImpl) :
    GetPagerUseCase {
    @OptIn(ExperimentalPagingApi::class)
    override fun invoke(last: String?): Flow<PagingData<MessageEntity>> = messagesRepository.getPager(last)
}