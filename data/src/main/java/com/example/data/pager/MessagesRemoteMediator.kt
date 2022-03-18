package com.example.data.pager

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.data.database.MessagesDatabase
import com.example.data.database.model.MessageEntity
import com.example.data.database.model.RemoteKeysEntity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalPagingApi::class)
class MessagesRemoteMediator(
    private val database: MessagesDatabase,
    private val getMessages: (last: String) -> Flow<List<MessageEntity>>
) : RemoteMediator<Int, MessageEntity>() {
    val messagesDao = database.messagesDao
    val remoteKeysDao = database.remoteKeysDao

    override suspend fun initialize(): InitializeAction {
        return super.initialize()
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MessageEntity>
    ): MediatorResult {
        try {
            val last = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextKey ?: ""
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)

                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    val nextPage = remoteKeys?.nextKey
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKeys != null
                        )
                    nextPage
                }
            }

            val response = getMessages(last)
            var messagesFromResponse = listOf<MessageEntity>()
            var endOfPaginationReached = false
            coroutineScope {
                launch {
                    response.collect {
                        endOfPaginationReached = it.isEmpty()
                        messagesFromResponse = it
                        database.withTransaction {
                            if (loadType == LoadType.REFRESH) {
                                messagesDao.deleteAllMessages()
                                remoteKeysDao.deleteAllRemoteKeys()
                            }
                            val keys = messagesFromResponse.map { message ->
                                RemoteKeysEntity(
                                    repoId = message.id,
                                    prevKey = null,
                                    nextKey = messagesFromResponse.elementAt(messagesFromResponse.size - 1).id
                                )
                            }
                            remoteKeysDao.addAllRemoteKeys(remoteKeys = keys)
                            messagesDao.insertAll(messagesFromResponse)
                        }
                        this.cancel()
                    }
                }
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, MessageEntity>
    ): RemoteKeysEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                remoteKeysDao.remoteKeysCharacterId(id = id)

            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, MessageEntity>
    ): RemoteKeysEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { message ->
                remoteKeysDao.remoteKeysCharacterId(message.id)
            }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, MessageEntity>
    ): RemoteKeysEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { message ->
                remoteKeysDao.remoteKeysCharacterId(id = message.id)
            }
    }
}