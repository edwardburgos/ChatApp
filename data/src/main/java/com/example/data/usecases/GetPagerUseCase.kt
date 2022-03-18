package com.example.data.usecases

import androidx.paging.PagingData
import com.example.data.database.model.MessageEntity
import kotlinx.coroutines.flow.Flow

interface GetPagerUseCase {
    operator fun invoke(last: String?): Flow<PagingData<MessageEntity>>
}