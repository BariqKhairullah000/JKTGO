package com.bangkitbariq.jktgo.chatbot

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangkitbariq.jktgo.R

class ChatbotActivity : AppCompatActivity() {

    private val chatMessages = mutableListOf<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button

    private var userName: String? = null
    private var isAskingForName = true // Flag to track if we are waiting for the user's name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)

        // Initialize views
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)

        // Set up RecyclerView and adapter
        chatAdapter = ChatAdapter(chatMessages)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = chatAdapter

        // Add initial bot message asking for user's name
        addBotMessage("Hello! What's your name?")

        // Set up the send button click listener
        sendButton.setOnClickListener {
            val userMessage = messageInput.text.toString()
            if (userMessage.isNotBlank()) {
                handleUserMessage(userMessage)
                messageInput.text.clear()
            }
        }
    }

    private fun handleUserMessage(message: String) {
        // Add user message to chat
        chatMessages.add(ChatMessage(message, isUser = true))
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
        chatRecyclerView.scrollToPosition(chatMessages.size - 1)

        // Check if the bot is asking for the user's name
        if (isAskingForName) {
            userName = message // Store the user's name
            isAskingForName = false // Turn off the flag

            // Respond with a personalized message
            addBotMessage("Nice to meet you, $userName! Is there anything I can help you with?")
        } else {
            // Handle other user messages here or add further bot responses as needed
            addBotMessage("How can I assist you today, $userName?")
        }
    }

    private fun addBotMessage(message: String) {
        // Add bot message to chat
        chatMessages.add(ChatMessage(message, isUser = false))
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
        chatRecyclerView.scrollToPosition(chatMessages.size - 1)
    }
}
