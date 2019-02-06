package com.google.firebase.udacity.friendlychat

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
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
            convertView!!.messageTextView.visibility = View.GONE
            convertView.photoImageView.visibility = View.VISIBLE
            Glide.with(convertView.photoImageView.context)
                    .load(message.photoUrl)
                    .into(convertView.photoImageView)
        } else {
            convertView!!.messageTextView.visibility = View.VISIBLE
            convertView.photoImageView.visibility = View.GONE
            convertView.messageTextView.text = message.text
        }
        convertView.nameTextView.text = message.name

        return convertView
    }
}
