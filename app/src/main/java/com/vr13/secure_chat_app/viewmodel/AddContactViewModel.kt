package com.vr13.secure_chat_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ContactUser(
    val uid: String = "",
    val username: String = "",
    val name: String = "",
    val photoUrl: String? = null
)

sealed class AddContactState {
    object Idle : AddContactState()
    object Loading : AddContactState()
    data class Success(val message: String) : AddContactState()
    data class Error(val message: String) : AddContactState()
}

class AddContactViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth

    private val _searchResults = MutableStateFlow<List<ContactUser>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _addContactState = MutableStateFlow<AddContactState>(AddContactState.Idle)
    val addContactState = _addContactState.asStateFlow()

    /**
     * Search Firestore for users by their username
     */
    fun searchUsers(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        _addContactState.value = AddContactState.Loading

        firestore.collection("users")
            .whereGreaterThanOrEqualTo("username", query)
            .whereLessThanOrEqualTo("username", query + "\uf8ff")
            .get()
            .addOnSuccessListener { result ->
                val users = result.documents.mapNotNull { doc ->
                    doc.toObject(ContactUser::class.java)?.copy(uid = doc.id)
                }.filter { it.uid != auth.currentUser?.uid } // exclude self
                _searchResults.value = users
                _addContactState.value = AddContactState.Idle
            }
            .addOnFailureListener { e ->
                _addContactState.value = AddContactState.Error(e.message ?: "Search failed")
            }
    }

    /**
     * Add contact by creating a chat room
     */
    fun addContact(otherUserId: String, onComplete: (String) -> Unit) {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            _addContactState.value = AddContactState.Error("Not logged in")
            return
        }

        // Check if chat exists
        firestore.collection("chats")
            .whereArrayContains("participants", currentUserId)
            .get()
            .addOnSuccessListener { result ->
                val existingChat = result.documents.find { doc ->
                    val participants = doc.get("participants") as? List<*>
                    participants?.contains(otherUserId) == true
                }
                if (existingChat != null) {
                    // already exists
                    onComplete(existingChat.id)
                } else {
                    // create new chat
                    val newChat = mapOf(
                        "participants" to listOf(currentUserId, otherUserId),
                        "lastMessage" to "",
                        "timestamp" to System.currentTimeMillis()
                    )
                    firestore.collection("chats")
                        .add(newChat)
                        .addOnSuccessListener { newChatRef ->
                            onComplete(newChatRef.id)
                        }
                        .addOnFailureListener { e ->
                            _addContactState.value = AddContactState.Error(e.message ?: "Failed to add contact")
                        }
                }
            }
            .addOnFailureListener { e ->
                _addContactState.value = AddContactState.Error(e.message ?: "Failed to check chats")
            }
    }
}
