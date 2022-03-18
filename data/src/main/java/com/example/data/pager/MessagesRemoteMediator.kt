package com.example.data.pager

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Sample RemoteMediator for a DB + Network based PagingData stream, which triggers network
 * requests to fetch additional items when a user scrolls to the end of the list of items stored
 * in DB.
 *
 * This sample loads a list of [User] items from an item-keyed Retrofit paginated source. This
 * source is "item-keyed" because we're loading the next page using information from the items
 * themselves (the ID param) as a key to fetch more data.
 */
@OptIn(ExperimentalPagingApi::class)
class MessagesRemoteMediator(
    private val query: String,
    private val database: RoomDb,
    private val networkService: ExampleBackendService
) : RemoteMediator<Int, User>() {
    val userDao = database.userDao()

    override suspend fun initialize(): InitializeAction {
        val cacheTimeout = TimeUnit.HOURS.convert(1, TimeUnit.MILLISECONDS)
        return if (System.currentTimeMillis() - userDao.lastUpdated() >= cacheTimeout) {
            // Cached data is up-to-date, so there is no need to re-fetch from network.
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            // Need to refresh cached data from network; returning LAUNCH_INITIAL_REFRESH here
            // will also block RemoteMediator's APPEND and PREPEND from running until REFRESH
            // succeeds.
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, DocumentSnapshot>
    ): MediatorResult {
        return try {
            // The network load method takes an optional `after=<user.id>` parameter. For every
            // page after the first, we pass the last user ID to let it continue from where it
            // left off. For REFRESH, pass `null` to load the first page.
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                // In this example, we never need to prepend, since REFRESH will always load the
                // first page in the list. Immediately return, reporting end of pagination.
                LoadType.PREPEND -> {
                    val lastItem = state.lastItemOrNull()

                    // We must explicitly check if the last item is `null` when appending,
                    // since passing `null` to networkService is only valid for initial load.
                    // If lastItem is `null` it means no items were loaded after the initial
                    // REFRESH and there are no more items to load.
                    if (lastItem == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    lastItem.id
                }
                LoadType.APPEND -> return MediatorResult.Success(endOfPaginationReached = true)

            }

            // Suspending network load via Retrofit. This doesn't need to be wrapped in a
            // withContext(Dispatcher.IO) { ... } block since Retrofit's Coroutine CallAdapter
            // dispatches on a worker thread.
//            val response = networkService.searchUsers(query = query, after = loadKey)
            val responseFlow = callbackFlow {
                var eventsCollection: Query? = null
                try {
                    eventsCollection = FirebaseFirestore.getInstance()
                        .collection("messages")
                        .orderBy("time", Query.Direction.DESCENDING)
                        .limit(10)
                    loadKey?.let { eventsCollection = eventsCollection?.startAfter(it) }
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
                        println("ESTOS SON LOS RESULTADOS")
                        println(snapshot.documents)
                        this.trySend(snapshot.documents).isSuccess

                    } catch (e: Throwable) {
                        Log.d("SUBSCRIPTION ERROR MESSAGE", e.message.toString())
                        Log.d("SUBSCRIPTION ERROR CAUSE", e.message.toString())
                    }
                }
                awaitClose { subscription?.remove() }
            }.flowOn(Dispatchers.IO)

            lateinit var response: List<DocumentSnapshot>
            responseFlow.collect {
                response = it
            }

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    userDao.deleteByQuery(query)
                }

                // Insert new users into database, which invalidates the current
                // PagingData, allowing Paging to present the updates in the DB.
                userDao.insertAll(response)
            }

            MediatorResult.Success(endOfPaginationReached = response.size != 11)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        }
//        catch (e: HttpException) {
//            MediatorResult.Error(e)
//        }
    }
}