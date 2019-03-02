package com.google.firebase.udacity.friendlychat.view

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.firebase.ui.auth.AuthUI
import com.google.firebase.udacity.friendlychat.R
import com.google.firebase.udacity.friendlychat.components.MarginItemDecoration
import com.google.firebase.udacity.friendlychat.components.chatRooms.ChatRoomsAdapter
import com.google.firebase.udacity.friendlychat.utils.Constants
import com.google.firebase.udacity.friendlychat.viewModels.RoomViewModel
import kotlinx.android.synthetic.main.activity_room.*

class RoomView : AppCompatActivity() {

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

        add_room.setOnClickListener { view ->
            viewModel.addRoom()
        }
        room_list.addItemDecoration(
                MarginItemDecoration(1))

        viewModel.listOfRooms.observeForever { rooms ->
            if (rooms != null) {
                progress_bar.visibility = View.GONE

                room_list.visibility = View.VISIBLE
                room_list.adapter = ChatRoomsAdapter(rooms)


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
}
