package com.bangkitbariq.jktgo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bangkitbariq.jktgo.MainActivity
import com.bangkitbariq.jktgo.chatbot.ChatbotActivity
import com.bangkitbariq.jktgo.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // FloatingActionButton untuk Chatbot
        val fabChatbot: FloatingActionButton = findViewById(R.id.fab_chatbot)
        fabChatbot.setOnClickListener {
            val intent = Intent(this, ChatbotActivity::class.java)
            startActivity(intent)
        }

        // BottomNavigationView untuk navigasi
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.item_profile // Tandai item aktif

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_dashboard -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Tutup ProfileActivity
                    true
                }
                R.id.item_profile -> true // Tetap di halaman ini
                else -> false
            }
        }
    }
}
