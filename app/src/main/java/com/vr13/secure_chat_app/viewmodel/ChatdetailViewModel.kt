package com.vr13.secure_chat_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
    val senderId: String = "",
    val message: String = "",
    val timestamp: Long = 0
)

class ChatDetailViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    fun observeMessages(chatId: String) {
        firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null && !snapshot.isEmpty) {
                    val result = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(ChatMessage::class.java)
                    }
                    _messages.value = result
                }
            }
    }

    fun sendMessage(chatId: String, messageText: String, senderId: String) {
        val message = ChatMessage(
            senderId = senderId,
            message = messageText,
            timestamp = System.currentTimeMillis()
        )
        viewModelScope.launch {
            firestore.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(message)
        }
    }
}
