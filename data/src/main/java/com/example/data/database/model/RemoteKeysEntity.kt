package com.example.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys_table")
data class RemoteKeysEntity(
    @PrimaryKey
    val repoId: String,

    @ColumnInfo
    val nextKey: String?,

    @ColumnInfo
    val prevKey: String?
)