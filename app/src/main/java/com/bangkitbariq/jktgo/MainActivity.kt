package com.bangkitbariq.jktgo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bangkitbariq.jktgo.R
import com.bangkitbariq.jktgo.chatbot.ChatbotActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fabChatbot: FloatingActionButton = findViewById(R.id.fab_chatbot)
        fabChatbot.setOnClickListener {
            val intent = Intent(this@MainActivity, ChatbotActivity::class.java)
            startActivity(intent)
        }
    }
}
