package com.google.firebase.udacity.friendlychat.components.chatRooms

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.google.firebase.udacity.friendlychat.utils.objects.ChatRoom


class ChatRoomsAdapter(private var chatRoomList: List<ChatRoom>) : RecyclerView.Adapter<ChatRoomViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        return ChatRoomViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return chatRoomList.size
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        holder.bindTo(chatRoomList[position])
    }


}

