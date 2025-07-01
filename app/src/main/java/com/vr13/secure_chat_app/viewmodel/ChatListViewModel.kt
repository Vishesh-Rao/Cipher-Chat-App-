package com.vr13.secure_chat_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.vr13.secure_chat_app.chatlist.ChatRoom
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatListViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _chatRooms = MutableStateFlow<List<ChatRoom>>(emptyList())
    val chatRooms = _chatRooms.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private var listener: ListenerRegistration? = null

    fun fetchChatsForUser(userId: String) {
        _isLoading.value = true
        _errorMessage.value = null

        // remove previous listener if exists
        listener?.remove()

        listener = db.collection("chats")
            .whereArrayContains("participants", userId)
            .addSnapshotListener { snapshot, error ->
                viewModelScope.launch {
                    if (error != null) {
                        _errorMessage.value = error.message
                        _isLoading.value = false
                        return@launch
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        val rooms = snapshot.documents.mapNotNull { doc ->
                            try {
                                doc.toObject(ChatRoom::class.java)?.copy(chatId = doc.id)
                            } catch (e: Exception) {
                                null
                            }
                        }
                        _chatRooms.value = rooms
                    } else {
                        _chatRooms.value = emptyList()
                    }

                    _isLoading.value = false
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        listener?.remove()
    }
}
