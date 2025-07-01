package com.vr13.secure_chat_app.secure

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import java.security.KeyPairGenerator
import java.security.KeyStore
import javax.crypto.*
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object SecureKeyManager {

    private const val RSA_KEY_ALIAS = "my_rsa_key"
    private const val AES_KEY_SIZE = 256
    private const val TAG = "SecureKeyManager"

    /**
     * Generate an RSA key pair if not exists
     */
    fun generateRSAKeyPair() {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        if (keyStore.containsAlias(RSA_KEY_ALIAS)) {
            Log.d(TAG, "RSA keypair already exists, skipping generation.")
            return
        }
        val kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore")
        val parameterSpec = KeyGenParameterSpec.Builder(
            RSA_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setKeySize(2048)
            .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
            .build()
        kpg.initialize(parameterSpec)
        kpg.generateKeyPair()
        Log.d(TAG, "RSA keypair generated successfully.")
    }

    /**
     * Generate a random AES key
     */
    fun generateAESKey(): SecretKey {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(AES_KEY_SIZE)
        return keyGen.generateKey()
    }

    /**
     * Encrypt (wrap) the AES key with RSA public key
     */
    fun wrapAESKey(aesKey: SecretKey): String {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        val publicKey = keyStore.getCertificate(RSA_KEY_ALIAS).publicKey

        val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val wrapped = cipher.doFinal(aesKey.encoded)

        return Base64.encodeToString(wrapped, Base64.NO_WRAP)
    }

    /**
     * Decrypt (unwrap) AES key using RSA private key
     * with safe fallback on corruption
     */
    fun unwrapAESKey(wrappedKeyBase64: String): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        val privateKey = keyStore.getKey(RSA_KEY_ALIAS, null)

        val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)

        return try {
            val wrappedBytes = Base64.decode(wrappedKeyBase64, Base64.NO_WRAP)
            val aesKeyBytes = cipher.doFinal(wrappedBytes)
            SecretKeySpec(aesKeyBytes, "AES")
        } catch (e: IllegalBlockSizeException) {
            // means wrappedKeyBase64 is invalid or corrupted
            Log.e(TAG, "unwrapAESKey failed, clearing corrupted RSA key", e)
            clearCorruptedKey()
            throw e
        }
    }

    /**
     * Clear corrupted RSA key from keystore
     */
    fun clearCorruptedKey() {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        if (keyStore.containsAlias(RSA_KEY_ALIAS)) {
            keyStore.deleteEntry(RSA_KEY_ALIAS)
            Log.d(TAG, "Corrupted RSA key cleared from keystore.")
        }
    }

    /**
     * Encrypt a message with AES GCM
     */
    fun encryptMessage(message: String, aesKey: SecretKey): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, aesKey)
        val iv = cipher.iv  // 12 bytes GCM standard
        val ciphertext = cipher.doFinal(message.toByteArray(Charsets.UTF_8))
        return iv + ciphertext // prefix IV
    }

    /**
     * Decrypt a message with AES GCM
     */
    fun decryptMessage(data: ByteArray, aesKey: SecretKey): String {
        val iv = data.sliceArray(0 until 12)
        val ciphertext = data.sliceArray(12 until data.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, aesKey, spec)
        val plaintext = cipher.doFinal(ciphertext)
        return String(plaintext, Charsets.UTF_8)
    }

    /**
     * Full best-practice pattern:
     * Load AES key with fallback if corrupted
     */
    fun getOrCreateAESKey(wrappedAESKeyPref: String?): SecretKey {
        generateRSAKeyPair() // ensure RSA exists

        return try {
            if (!wrappedAESKeyPref.isNullOrEmpty()) {
                unwrapAESKey(wrappedAESKeyPref)
            } else {
                val newKey = generateAESKey()
                Log.d(TAG, "Generated new AES key.")
                newKey
            }
        } catch (e: Exception) {
            Log.e(TAG, "AES key unwrap failed, regenerating.", e)
            val newKey = generateAESKey()
            newKey
        }
    }
}
