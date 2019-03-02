package com.google.firebase.udacity.friendlychat.view

import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.udacity.friendlychat.R
import com.google.firebase.udacity.friendlychat.components.MarginItemDecoration
import com.google.firebase.udacity.friendlychat.components.chatRooms.ChatRoomInterface
import com.google.firebase.udacity.friendlychat.components.chatRooms.ChatRoomsAdapter
import com.google.firebase.udacity.friendlychat.utils.Constants
import com.google.firebase.udacity.friendlychat.utils.objects.ChatRoomWithKey
import com.google.firebase.udacity.friendlychat.viewModels.RoomViewModel
import kotlinx.android.synthetic.main.activity_room.*
import kotlinx.android.synthetic.main.dialog_create_room.view.*


class RoomView : AppCompatActivity(), ChatRoomInterface {
    private var username = Constants.ANONYMOUS

    private val viewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this).get(RoomViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)

        val usernameExtra = intent.getStringExtra(Constants.USERNAME_PARAM)

        if (usernameExtra == null) {
            onBackPressed()
        } else {
            username = usernameExtra
        }
        viewModel.getRooms()

        room_list.addItemDecoration(
                MarginItemDecoration(1))
        room_list.adapter = ChatRoomsAdapter(roomListener = this)

        viewModel.listOfRooms.observeForever { rooms ->
            if (rooms != null) {
                progress_bar.visibility = View.GONE
                room_list.visibility = View.VISIBLE

                (room_list.adapter as ChatRoomsAdapter).updateRooms(rooms)
                (room_list.adapter as ChatRoomsAdapter).notifyDataSetChanged()
            }
        }

        add_room.setOnClickListener {
            customDialog()
        }
    }

    private fun customDialog() {

        val customView = layoutInflater.inflate(R.layout.dialog_create_room, null)

        val dialog = Dialog(this@RoomView)
        dialog.setContentView(customView) // your custom view.
        dialog.show()

        customView.cancel_button.setOnClickListener { dialog.cancel() }
        customView.create_button.setOnClickListener {
            if (customView.room_password.text.toString() != customView.room_confirm_password.text.toString()) {
                dialog.cancel()
                Toast.makeText(this,"Password and confirmation are different",Toast.LENGTH_SHORT).show()
            } else {
                viewModel.addRoom(username,customView.room_name.text.toString(),customView.room_description.text.toString(),customView.room_password.text.toString())
                dialog.cancel()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sign_out_menu -> {
                this@RoomView.onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        AuthUI.getInstance().signOut(this)
        val intent = Intent(this, StartView::class.java)
        startActivity(intent)
        finish()
    }

    override fun openRoom(room: ChatRoomWithKey) {
        val intent = Intent(this, ChatView::class.java)
        intent.putExtra(Constants.USERNAME_PARAM, username)
        intent.putExtra(Constants.ROOM_KEY_PARAM, room.key)
        intent.putExtra(Constants.ROOM_NAME_PARAM, room.room.title)
        startActivity(intent)
        finish()
    }
}
