package com.example.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.data.database.MessagesDatabase
import com.example.data.database.model.MessageEntity
import com.example.data.pager.MessagesRemoteMediator
import com.example.data.utils.DEFAULT_PAGE_SIZE
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.time.ExperimentalTime

class MessagesRepositoryImpl @Inject constructor(
    private val database: MessagesDatabase,
): MessagesRepository {

    private val messagesDao = database.messagesDao
    @OptIn(ExperimentalTime::class)
    override fun getMessages(last: String?) = callbackFlow {
            var eventsCollection: Query? = null
            try {
                eventsCollection = FirebaseFirestore.getInstance()
                    .collection("messages")
                    .orderBy("time", Query.Direction.ASCENDING)

                last?.let { if (last.isNotEmpty()) {
                   val dm = FirebaseFirestore.getInstance().collection("messages").document(last).get().await()
                                                   eventsCollection = eventsCollection?.startAfter(dm)?.limit(10)
                } else {
                    eventsCollection = eventsCollection?.limit(10)
                }
                }
            } catch (e: Throwable) {
                Log.d("ERROR MESSAGE", e.message.toString())
                Log.d("ERROR CAUSE", e.message.toString())
                close(e)
            }
            val subscription = eventsCollection?.addSnapshotListener { snapshot, _ ->
                if (snapshot == null) {
                    return@addSnapshotListener
                }
                try {
                    this.trySend(snapshot.documents.map {
                        MessageEntity(
                            id = it.id,
                            content = it.data?.get("message")?.let { message -> message.toString() } ?: run { "" },
                            image = it.data?.get("image")?.let { image -> image.toString() } ?: run { "" },
                            owner = it.data?.get("owner")?.let { owner -> owner.toString() } ?: run { "" },
                            read = it.data?.get("read")?.let { read -> read as Boolean } ?: run { false },
                            time = it.data?.get("time")?.let { time -> (time as com.google.firebase.Timestamp).seconds * 1000 } ?: run { 0L },
                        )
                    }).isSuccess

                } catch (e: Throwable) {
                    Log.d("SUBSCRIPTION ERROR MESSAGE", e.message.toString())
                    Log.d("SUBSCRIPTION ERROR CAUSE", e.message.toString())
                }
            }
            awaitClose { subscription?.remove() }
        }.flowOn(Dispatchers.IO)

    @ExperimentalPagingApi
    override fun getPager(last: String?): Flow<PagingData<MessageEntity>> {
        return Pager(config = PagingConfig(pageSize = DEFAULT_PAGE_SIZE),
            pagingSourceFactory = {
                messagesDao.getAllMessagesPaging()
            },
            remoteMediator = MessagesRemoteMediator(database) { getMessages(it) }
        ).flow
    }
}