package com.bangkitbariq.jktgo.chatbot

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkitbariq.jktgo.databinding.ActivityChatbotBinding
import com.bangkitbariq.jktgo.utils.NetworkUtils

class ChatbotActivity : AppCompatActivity() {
    private val viewModel: ChatbotViewModel by viewModels()
    private lateinit var binding: ActivityChatbotBinding
    private val adapter = ChatAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatbotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
        checkInternetConnection()
    }

    private fun setupUI() {
        binding.chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ChatbotActivity)
            adapter = this@ChatbotActivity.adapter
        }

        binding.sendButton.setOnClickListener {
            if (!NetworkUtils.isNetworkAvailable(this)) {
                Toast.makeText(this, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val message = binding.messageInput.text.toString()
            if (message.isNotEmpty()) {
                viewModel.sendMessage(message)
                binding.messageInput.text.clear()
            }
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun observeViewModel() {
        viewModel.messages.observe(this) { messages ->
            adapter.updateMessages(messages)
            binding.chatRecyclerView.scrollToPosition(messages.size - 1)
        }

        viewModel.networkState.observe(this) { state ->
            when (state) {
                NetworkState.LOADING -> binding.progressBar.visibility = View.VISIBLE
                NetworkState.IDLE -> binding.progressBar.visibility = View.GONE
                NetworkState.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Terjadi kesalahan jaringan", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkInternetConnection() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "Tidak ada koneksi internet", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val TAG = "ChatbotActivity"
    }
}