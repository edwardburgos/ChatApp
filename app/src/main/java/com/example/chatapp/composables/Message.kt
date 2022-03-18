package com.example.chatapp.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.util.*

@Composable
fun Message(
    content: String,
    owner: String,
    read: Boolean,
    time: Long
) {
    val fecha = Date()
    fecha.setTime(time)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (owner == "EDWARD") Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .clip(
                    RoundedCornerShape(10.dp)
                )
                .background(if (owner == "EDWARD") Color.Blue else Color.Gray)
        ) {
            Text(
                text = content,
                color = if (owner == "EDWARD") Color.White else Color.Black,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp)
            )
            Row(
                modifier = Modifier.padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 8.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    "${fecha.hours}:${fecha.minutes}",
                    color = if (owner == "EDWARD") Color.White else Color.Black,
                    modifier = Modifier.padding(end = 4.dp)
                )
                if (owner == "EDWARD") {
                    Icon(
                        Icons.Filled.DoneAll,
                        contentDescription = null,
                        tint = if (read) Color(0xFF2E4DFF) else Color.Gray
                    )
                }
            }
        }
    }
}

