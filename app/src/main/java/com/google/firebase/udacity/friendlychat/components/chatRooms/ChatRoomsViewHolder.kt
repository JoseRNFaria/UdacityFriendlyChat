package com.google.firebase.udacity.friendlychat.components.chatRooms

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.udacity.friendlychat.R
import com.google.firebase.udacity.friendlychat.utils.objects.ChatRoomWithKey
import kotlinx.android.synthetic.main.item_chat_room.view.*

class ChatRoomsViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_chat_room, parent, false)
) {
    /**
     * Items might be null if they are not paged in yet. PagedListAdapter will re-bind the
     * ViewHolder when Item is loaded.
     */
    fun bindTo(username:String, room: ChatRoomWithKey, roomsListener: ChatRoomsInterface) {
        itemView.room_name.text = room.room.title
        itemView.room_description.text = room.room.description

        itemView.setOnClickListener {
            roomsListener.openRoom(room)
        }

        if (room.room.password.isEmpty()) {
            itemView.room_password.setImageResource(R.drawable.ic_lock_open_black)
        }
        else
        {
            itemView.room_password.setImageResource(R.drawable.ic_lock_outline_black)
        }




        if(username==room.room.user) {
            itemView.more_options.isEnabled=true
            itemView.more_options.setOnClickListener {
                roomsListener.roomOptions(room)
            }
            itemView.setOnLongClickListener {
                roomsListener.roomOptions(room)
                return@setOnLongClickListener true
            }
        }
        else
        {
            itemView.more_options.isEnabled=false
            itemView.more_options.setOnClickListener(null)
            itemView.setOnLongClickListener(null)
        }


    }
}


