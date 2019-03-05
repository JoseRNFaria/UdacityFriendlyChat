package com.google.firebase.udacity.friendlychat.components.chatMessages

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.udacity.friendlychat.R
import com.google.firebase.udacity.friendlychat.utils.objects.FriendlyMessage
import kotlinx.android.synthetic.main.item_message.view.*

class MessageChatViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
) {
    /**
     * Items might be null if they are not paged in yet. PagedListAdapter will re-bind the
     * ViewHolder when Item is loaded.
     */
    fun bindTo(message: FriendlyMessage) {
        val isPhoto = message.photoUrl != null
        if (isPhoto) {
            itemView.message_text_view.visibility = View.GONE
            itemView.photo_image_view.visibility = View.VISIBLE
            Glide.with(itemView.photo_image_view.context)
                    .load(message.photoUrl)
                    .into(itemView.photo_image_view)
        } else {
            itemView.message_text_view.visibility = View.VISIBLE
            itemView.photo_image_view.visibility = View.GONE
            itemView.message_text_view.text = message.text
        }
        itemView.name_text_view.text = message.name

    }
}


