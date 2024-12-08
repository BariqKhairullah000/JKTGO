package com.bangkitbariq.jktgo.chatbot

data class ChatMessage(
    val text: String,
    val isUser: Boolean = true,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String? = null
)