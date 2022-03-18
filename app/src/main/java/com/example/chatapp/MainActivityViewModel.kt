package com.example.chatapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.data.database.model.MessageEntity
import com.example.data.usecases.GetMessagesUseCase
import com.example.data.usecases.GetPagerUseCase
import com.example.domain.Owners
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val getMessages: GetMessagesUseCase,
    private val getPager: GetPagerUseCase
) : ViewModel() {

    var db = FirebaseFirestore.getInstance()

    var messages: Flow<PagingData<MessageEntity>> = getPager.invoke(null).cachedIn(viewModelScope)

    fun sendToFirebase(content: String) {
        val message: MutableMap<String, Any> = HashMap()
        message["image"] = ""
        message["message"] = content
        message["owner"] = Owners.EDWARD
        message["read"] = false
        message["time"] = Calendar.getInstance().time

        db.collection("messages")
            .add(message)
            .addOnSuccessListener {
                Log.d("LOG", "Successful message sending")
            }
            .addOnFailureListener {
                Log.d("LOG", "Error sending message")
            }
    }
}
