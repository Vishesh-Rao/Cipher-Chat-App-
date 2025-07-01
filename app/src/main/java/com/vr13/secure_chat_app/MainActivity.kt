package com.vr13.secure_chat_app

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.vr13.secure_chat_app.navigationsystem.SecureChatNavGraph
import com.vr13.secure_chat_app.ui.theme.Secure_Chat_AppTheme
import com.vr13.secure_chat_app.secure.SecureKeyManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Secure_Chat_AppTheme {
                val navController = rememberNavController()
                SecureChatNavGraph(navController = navController)
            }
        }

        // print current Firebase UID
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        Log.d("DEBUG", "🔥 My current Firebase UID is: $userId")

        // Secure key management
        SecureKeyManager.generateRSAKeyPair()

        val prefs = getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
        val wrappedAESKey = prefs.getString("wrapped_aes_key", null)

        val aesKey = try {
            if (wrappedAESKey != null) {
                SecureKeyManager.unwrapAESKey(wrappedAESKey)
            } else {
                val newAES = SecureKeyManager.generateAESKey()
                val newWrapped = SecureKeyManager.wrapAESKey(newAES)
                prefs.edit().putString("wrapped_aes_key", newWrapped).apply()
                newAES
            }
        } catch (e: Exception) {
            e.printStackTrace()
            SecureKeyManager.clearCorruptedKey()
            SecureKeyManager.generateRSAKeyPair()
            val freshAES = SecureKeyManager.generateAESKey()
            val freshWrapped = SecureKeyManager.wrapAESKey(freshAES)
            prefs.edit().putString("wrapped_aes_key", freshWrapped).apply()
            freshAES
        }

        // sample test encryption
        val testCiphertext = SecureKeyManager.encryptMessage("hello world", aesKey)
        val testPlaintext = SecureKeyManager.decryptMessage(testCiphertext, aesKey)

        Log.d("ENCRYPTION_TEST", "Ciphertext: ${testCiphertext.contentToString()}")
        Log.d("ENCRYPTION_TEST", "Plaintext: $testPlaintext")
    }
}
