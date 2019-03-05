package com.google.firebase.udacity.friendlychat.components.chatRooms

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.google.firebase.udacity.friendlychat.utils.objects.ChatRoomWithKey


class ChatRoomsAdapter(private val username:String,private var chatRoomList: List<ChatRoomWithKey> = listOf(),private val roomListener:ChatRoomInterface) : RecyclerView.Adapter<ChatRoomViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        return ChatRoomViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return chatRoomList.size
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        holder.bindTo(username,chatRoomList[position],roomListener)
    }

    fun updateRooms(newChatRoomList: List<ChatRoomWithKey>) {
        chatRoomList = newChatRoomList
    }


}

