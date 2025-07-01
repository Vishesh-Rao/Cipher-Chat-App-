package com.vr13.secure_chat_app.chatlist

import androidx.annotation.Keep

@Keep
data class ChatRoom(
    val chatId: String = "",
    val participants: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastTimestamp: Long = 0L
)
