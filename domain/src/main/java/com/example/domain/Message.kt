package com.example.domain

import java.util.UUID
import java.util.Date

data class Message(
    var id: UUID,
    var content: String,
    var image: String,
    var owner: String,
    var read: Boolean,
    var time: Date

)