package com.google.firebase.udacity.friendlychat.components.chatRooms

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.udacity.friendlychat.R
import com.google.firebase.udacity.friendlychat.utils.objects.ChatRoom
import kotlinx.android.synthetic.main.item_chat_room.view.*

class ChatRoomViewHolder(var parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_chat_room, parent, false)
) {
    /**
     * Items might be null if they are not paged in yet. PagedListAdapter will re-bind the
     * ViewHolder when Item is loaded.
     */
    fun bindTo(room: ChatRoom) {
        itemView.room_name.text = room.title
        itemView.room_description.text = room.description
    }
}


