package com.example.data.usecases

import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow

interface GetMessagesUseCase {
    operator fun invoke(last: DocumentSnapshot?): Flow<List<DocumentSnapshot>>
}