package com.google.firebase.udacity.friendlychat.components

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.google.firebase.udacity.friendlychat.R
import com.google.firebase.udacity.friendlychat.utils.FriendlyMessage
import kotlinx.android.synthetic.main.item_message.view.*

class MessageAdapter(context: Context, resource: Int, objects: List<FriendlyMessage>) : ArrayAdapter<FriendlyMessage>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            convertView = (context as Activity).layoutInflater.inflate(R.layout.item_message, parent, false)
        }

        val message = getItem(position)

        val isPhoto = message!!.photoUrl != null
        if (isPhoto) {
            convertView!!.message_text_view.visibility = View.GONE
            convertView.photo_image_view.visibility = View.VISIBLE
            Glide.with(convertView.photo_image_view.context)
                    .load(message.photoUrl)
                    .into(convertView.photo_image_view)
        } else {
            convertView!!.message_text_view.visibility = View.VISIBLE
            convertView.photo_image_view.visibility = View.GONE
            convertView.message_text_view.text = message.text
        }
        convertView.name_text_view.text = message.name

        return convertView
    }
}
