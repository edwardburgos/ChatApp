package com.example.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun getMessages(last: DocumentSnapshot?): Flow<List<DocumentSnapshot>>
}