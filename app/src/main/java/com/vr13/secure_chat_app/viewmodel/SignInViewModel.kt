package com.vr13.secure_chat_app.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit

class SignInViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    // State holders
    private val _verificationId = MutableStateFlow<String?>(null)
    val verificationId = _verificationId.asStateFlow()

    private val _signInState = MutableStateFlow<SignInState>(SignInState.Idle)
    val signInState = _signInState.asStateFlow()

    fun sendOtp(phoneNumber: String, activity: Activity) {
        _signInState.value = SignInState.Loading

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Instant verification
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    _signInState.value = SignInState.Error(e.message ?: "Verification failed")
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    _verificationId.value = verificationId
                    _signInState.value = SignInState.CodeSent
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(otp: String) {
        val verification = _verificationId.value
        if (verification != null) {
            val credential = PhoneAuthProvider.getCredential(verification, otp)
            signInWithPhoneAuthCredential(credential)
        } else {
            _signInState.value = SignInState.Error("No verification ID available.")
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _signInState.value = SignInState.Success
                } else {
                    _signInState.value = SignInState.Error(
                        task.exception?.message ?: "Invalid OTP or sign-in error"
                    )
                }
            }
    }
}

sealed class SignInState {
    object Idle : SignInState()
    object Loading : SignInState()
    object CodeSent : SignInState()
    object Success : SignInState()
    data class Error(val message: String) : SignInState()
}
