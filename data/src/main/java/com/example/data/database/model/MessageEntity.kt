package com.example.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages_table")
data class MessageEntity(
    @PrimaryKey
    var id: String,

    @ColumnInfo
    var content: String,

    @ColumnInfo
    var image: String,

    @ColumnInfo
    var owner: String,

    @ColumnInfo
    var read: Boolean,

    @ColumnInfo
    var time: Long
)