package com.vr13.secure_chat_app.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditProfileViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess = _isSuccess.asStateFlow()

    fun saveProfile(name: String, bio: String, imageUri: Uri?) {
        _isLoading.value = true
        _errorMessage.value = null

        val uid = auth.currentUser?.uid
        if (uid == null) {
            _errorMessage.value = "User not signed in."
            _isLoading.value = false
            return
        }

        viewModelScope.launch {
            try {
                // If image is selected, upload it
                if (imageUri != null) {
                    val ref = storage.child("profileImages/$uid.jpg")
                    ref.putFile(imageUri)
                        .addOnSuccessListener {
                            ref.downloadUrl.addOnSuccessListener { downloadUrl ->
                                updateUserProfile(name, bio, downloadUrl.toString(), uid)
                            }
                        }
                        .addOnFailureListener { e ->
                            _isLoading.value = false
                            _errorMessage.value = e.message ?: "Image upload failed"
                        }
                } else {
                    // no image, just update name/bio
                    updateUserProfile(name, bio, null, uid)
                }

            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = e.localizedMessage
            }
        }
    }

    private fun updateUserProfile(name: String, bio: String, profileImageUrl: String?, uid: String) {
        val userMap = mutableMapOf<String, Any?>(
            "name" to name,
            "bio" to bio
        )
        profileImageUrl?.let { userMap["photoUrl"] = it }

        firestore.collection("users").document(uid)
            .update(userMap)
            .addOnSuccessListener {
                _isLoading.value = false
                _isSuccess.value = true
            }
            .addOnFailureListener { e ->
                _isLoading.value = false
                _errorMessage.value = e.localizedMessage
            }
    }
}
