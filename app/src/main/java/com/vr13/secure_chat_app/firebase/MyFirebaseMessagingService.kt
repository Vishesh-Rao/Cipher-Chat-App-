package com.vr13.secure_chat_app.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Refreshed token: $token")
        // Optionally send the token to your server
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // Handle the push notification
        val title = remoteMessage.notification?.title
        val body = remoteMessage.notification?.body
        Log.d("FCM", "Notification received: $title - $body")
        // optionally show notification
    }
}
