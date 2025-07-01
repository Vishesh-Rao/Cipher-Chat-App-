package com.vr13.secure_chat_app.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit

class SignUpViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _verificationId = MutableStateFlow<String?>(null)
    val verificationId = _verificationId.asStateFlow()

    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val signUpState = _signUpState.asStateFlow()

    fun sendOtp(phoneNumber: String, activity: Activity) {
        _signUpState.value = SignUpState.Loading

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithCredential(credential, null)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    _signUpState.value = SignUpState.Error(e.message ?: "Verification failed")
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    _verificationId.value = verificationId
                    _signUpState.value = SignUpState.CodeSent
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(otp: String, userName: String) {
        val verification = _verificationId.value
        if (verification != null) {
            val credential = PhoneAuthProvider.getCredential(verification, otp)
            signInWithCredential(credential, userName)
        } else {
            _signUpState.value = SignUpState.Error("No verification ID available.")
        }
    }

    private fun signInWithCredential(credential: PhoneAuthCredential, userName: String?) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // user is authenticated, create Firestore profile
                    val userId = firebaseAuth.currentUser?.uid
                    val phoneNumber = firebaseAuth.currentUser?.phoneNumber
                    if (userId != null && userName != null && phoneNumber != null) {
                        val userMap = mapOf(
                            "uid" to userId,
                            "name" to userName,
                            "phone" to phoneNumber,
                            "createdAt" to System.currentTimeMillis()
                        )
                        firestore.collection("users").document(userId)
                            .set(userMap)
                            .addOnSuccessListener {
                                _signUpState.value = SignUpState.Success
                            }
                            .addOnFailureListener { e ->
                                _signUpState.value = SignUpState.Error(
                                    e.message ?: "Failed to create user profile"
                                )
                            }
                    } else {
                        _signUpState.value = SignUpState.Error("User info missing")
                    }
                } else {
                    _signUpState.value = SignUpState.Error("Invalid OTP or sign-in error")
                }
            }
    }
}

sealed class SignUpState {
    object Idle : SignUpState()
    object Loading : SignUpState()
    object CodeSent : SignUpState()
    object Success : SignUpState()
    data class Error(val message: String) : SignUpState()
}
