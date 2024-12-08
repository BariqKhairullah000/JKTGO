package com.bangkitbariq.jktgo.chatbot

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bangkitbariq.jktgo.R

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    private val messages = mutableListOf<ChatMessage>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_chat_bubble,
            parent,
            false
        )
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int = messages.size

    fun updateMessages(newMessages: List<ChatMessage>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.message_text)

        fun bind(message: ChatMessage) {
            messageText.text = message.text

            val layoutParams = messageText.layoutParams as FrameLayout.LayoutParams
            if (message.isUser) {
                messageText.background = ContextCompat.getDrawable(
                    messageText.context,
                    R.drawable.bg_chat_bubble_user
                )
                layoutParams.gravity = Gravity.END
            } else {
                messageText.background = ContextCompat.getDrawable(
                    messageText.context,
                    R.drawable.bg_chat_bubble_bot
                )
                layoutParams.gravity = Gravity.START
            }
            messageText.layoutParams = layoutParams
        }
    }
}
