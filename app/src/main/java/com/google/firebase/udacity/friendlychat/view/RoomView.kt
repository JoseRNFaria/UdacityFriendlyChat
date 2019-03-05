package com.google.firebase.udacity.friendlychat.view

import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.SearchView
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.udacity.friendlychat.R
import com.google.firebase.udacity.friendlychat.components.MarginItemDecoration
import com.google.firebase.udacity.friendlychat.components.chatRooms.ChatRoomsInterface
import com.google.firebase.udacity.friendlychat.components.chatRooms.ChatRoomsAdapter
import com.google.firebase.udacity.friendlychat.utils.Constants
import com.google.firebase.udacity.friendlychat.utils.objects.ChatRoomWithKey
import com.google.firebase.udacity.friendlychat.viewModels.RoomViewModel
import kotlinx.android.synthetic.main.activity_room.*
import kotlinx.android.synthetic.main.dialog_create_room.view.*
import kotlinx.android.synthetic.main.dialog_password_room.view.*


class RoomView : AppCompatActivity(), ChatRoomsInterface {

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
        room_list.adapter = ChatRoomsAdapter(username, roomsListener = this)

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

        val customView = layoutInflater.inflate(com.google.firebase.udacity.friendlychat.R.layout.dialog_create_room, null)

        val dialog = Dialog(this@RoomView)
        dialog.setContentView(customView) // your custom view.
        dialog.show()

        val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)

        customView.cancel_button.setOnClickListener { dialog.cancel() }

        customView.create_button.setOnClickListener {
            when {
                customView.room_name.text.toString().isEmpty() -> customView.room_name.error = getString(R.string.error_empty_field)
                customView.room_password.text.toString() != customView.room_confirm_password.text.toString() -> customView.room_confirm_password.error = getString(R.string.error_different_passwords)
                else -> {
                    viewModel.addRoom(username, customView.room_name.text.toString(), customView.room_description.text.toString(), customView.room_password.text.toString())
                    dialog.cancel()
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.room_menu, menu)

        // Associate searchable configuration with the SearchView
        val searchView = menu.findItem(R.id.search).actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.search(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.search(newText)
                return true
            }

        })

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
        AuthUI.getInstance().signOut(this).addOnSuccessListener {
            val intent = Intent(this, StartView::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun openRoom(room: ChatRoomWithKey) {

        if (room.room.password.isNotEmpty()) {
            val customView = layoutInflater.inflate(R.layout.dialog_password_room, null)

            val dialog = Dialog(this@RoomView)
            dialog.setContentView(customView) // your custom view.
            dialog.show()

            val window = dialog.window
            window?.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)

            customView.join_password_button.setOnClickListener {
                if (room.room.password == customView.room_input_password.text.toString()) {
                    enterRoom(room)
                } else {
                    Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show()
                }
                dialog.cancel()
            }

            customView.cancel_password_button.setOnClickListener {
                dialog.cancel()
            }


        } else {
            enterRoom(room)
        }

    }

    private fun enterRoom(room: ChatRoomWithKey) {
        val intent = Intent(this, ChatView::class.java)
        intent.putExtra(Constants.USERNAME_PARAM, username)
        intent.putExtra(Constants.ROOM_KEY_PARAM, room.key)
        intent.putExtra(Constants.ROOM_NAME_PARAM, room.room.title)
        startActivity(intent)
        finish()
    }

    override fun roomOptions(room: ChatRoomWithKey) {


        Toast.makeText(this, "long click", Toast.LENGTH_SHORT).show()
    }
}
