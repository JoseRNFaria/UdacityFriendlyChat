package com.google.firebase.udacity.friendlychat.components.chatMessages

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.google.firebase.udacity.friendlychat.utils.objects.FriendlyMessage

class MessageChatAdapter(private var messages: MutableList<FriendlyMessage> = mutableListOf()) : RecyclerView.Adapter<MessageChatViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageChatViewHolder {
        return MessageChatViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: MessageChatViewHolder, position: Int) {
        holder.bindTo(messages[position])
    }

    fun updateMessages(newMessages: MutableList<FriendlyMessage>) {
        messages = newMessages
        notifyDataSetChanged()
    }


}

