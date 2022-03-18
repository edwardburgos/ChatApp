package com.example.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.database.model.RemoteKeysEntity

@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllRemoteKeys(remoteKeys: List<RemoteKeysEntity>)

    @Query("SELECT * FROM remote_keys_table WHERE repoId = :id")
    suspend fun remoteKeysCharacterId(id: String): RemoteKeysEntity

    @Query("DELETE FROM remote_keys_table")
    suspend fun deleteAllRemoteKeys()
}
