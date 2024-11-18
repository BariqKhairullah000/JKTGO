package com.bangkitbariq.jktgo.chatbot

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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
    private lateinit var backButton: ImageView // Tombol back di ImageView

    private var userName: String? = null
    private var isAskingForName = true // Flag untuk melacak apakah bot sedang menunggu nama pengguna

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)

        // Inisialisasi komponen tampilan
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)
        backButton = findViewById(R.id.backButton)  // Inisialisasi tombol back

        // Set up RecyclerView dan adapter
        chatAdapter = ChatAdapter(chatMessages)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = chatAdapter

        // Tambahkan pesan awal dari bot
        addBotMessage("Hello! What's your name?")

        // Listener untuk tombol kirim
        sendButton.setOnClickListener {
            val userMessage = messageInput.text.toString()
            if (userMessage.isNotBlank()) {
                handleUserMessage(userMessage)
                messageInput.text.clear()
            }
        }

        // Listener untuk tombol back
        backButton.setOnClickListener {
            finish() // Kembali ke halaman sebelumnya
        }
    }

    private fun handleUserMessage(message: String) {
        // Tambahkan pesan pengguna ke chat
        chatMessages.add(ChatMessage(message, isUser = true))
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
        chatRecyclerView.scrollToPosition(chatMessages.size - 1)

        // Cek apakah bot sedang meminta nama pengguna
        if (isAskingForName) {
            userName = message // Simpan nama pengguna
            isAskingForName = false // Matikan flag

            // Respon dengan pesan personalisasi
            addBotMessage("Nice to meet you, $userName! Is there anything I can help you with?")
        } else {
            // Respon untuk pesan lainnya
            addBotMessage("How can I assist you today, $userName?")
        }
    }

    private fun addBotMessage(message: String) {
        // Tambahkan pesan bot ke chat
        chatMessages.add(ChatMessage(message, isUser = false))
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
        chatRecyclerView.scrollToPosition(chatMessages.size - 1)
    }
}
