// ChatAdapter.kt
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

class ChatAdapter(private val chatList: List<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_chat_bubble, parent, false
        )
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatMessage = chatList[position]
        holder.messageText.text = chatMessage.message

        // Set bubble appearance and alignment based on message type (user or bot)
        val layoutParams = holder.messageText.layoutParams as FrameLayout.LayoutParams
        if (chatMessage.isUser) {
            // User message: align to the right with green background
            holder.messageText.background = ContextCompat.getDrawable(holder.messageText.context, R.drawable.bg_chat_bubble_user)
            layoutParams.gravity = Gravity.END
            holder.messageText.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
        } else {
            // Bot message: align to the left with blue background
            holder.messageText.background = ContextCompat.getDrawable(holder.messageText.context, R.drawable.bg_chat_bubble_bot)
            layoutParams.gravity = Gravity.START
            holder.messageText.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        }
        holder.messageText.layoutParams = layoutParams
    }

    override fun getItemCount() = chatList.size

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.message_text)
    }
}


