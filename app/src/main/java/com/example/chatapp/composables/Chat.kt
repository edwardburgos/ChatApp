package com.example.chatapp.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.example.data.database.model.MessageEntity

@Composable
fun Chat(
    messages: LazyPagingItems<MessageEntity>,
    sendToFirebase: (String) -> Unit
) {

    val message = remember {
        mutableStateOf("Hola")
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(messages) { message ->
                message?.let {
                        Message(
                            it.content,
                            it.owner,
                            it.read,
                            it.time
                        )
                }
            }
            messages.apply {
                when {
                    loadState.refresh is LoadState.Loading || loadState.append is LoadState.Loading -> {
                        item(1) {
                            Text("CARGANDO")
                        }
                    }
                    loadState.refresh is LoadState.Error || loadState.append is LoadState.Error -> {
                        item(1) { Text("ERROR") }
                    }
                }
            }
        }
        Row(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = message.value,
                onValueChange = { message.value = it },
                modifier = Modifier.padding(end = 8.dp).weight(1f)
            )
            IconButton(onClick = { sendToFirebase(message.value) }, modifier = Modifier.border(1.dp, MaterialTheme.colors.primary, shape = CircleShape)) {
                Icon(
                    Icons.Filled.Send,
                    contentDescription = "Send",
                    tint = MaterialTheme.colors.primary
                )
            }
        }
    }
}