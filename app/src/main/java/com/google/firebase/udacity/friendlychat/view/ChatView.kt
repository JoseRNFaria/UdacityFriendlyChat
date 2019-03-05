/**
 * Copyright Google Inc. All Rights Reserved.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.firebase.udacity.friendlychat.view

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import com.firebase.ui.auth.AuthUI
import com.google.firebase.udacity.friendlychat.R
import com.google.firebase.udacity.friendlychat.components.chatMessages.MessageChatAdapter
import com.google.firebase.udacity.friendlychat.utils.Constants
import com.google.firebase.udacity.friendlychat.viewModels.ChatViewModel
import kotlinx.android.synthetic.main.activity_chat.*

class ChatView : AppCompatActivity() {

    private var username = Constants.ANONYMOUS
    private lateinit var messageChatAdapter: MessageChatAdapter

    private val viewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this).get(ChatViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val usernameExtra = intent.getStringExtra(Constants.USERNAME_PARAM)
        val roomKeyExtra = intent.getStringExtra(Constants.ROOM_KEY_PARAM)
        val roomNameExtra = intent.getStringExtra(Constants.ROOM_NAME_PARAM)

        if (usernameExtra == null) {
            loggedOut()
        } else {
            username = usernameExtra
        }

        title = roomNameExtra
        viewModel.getMessages(roomKeyExtra)

        messageChatAdapter = MessageChatAdapter(mutableListOf())
        message_list_view.adapter = messageChatAdapter

        viewModel.chatMessages.observeForever { messages ->
            if (messages != null) {
                messageChatAdapter.updateMessages(messages)
            }
        }

        viewModel.msgLength.observeForever { size ->
            if (size != null) {
                message_edit_text.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(size))
            }
        }

        // Initialize progress bar
        progress_bar.visibility = ProgressBar.INVISIBLE


        // Enable Send button when there's text to send
        message_edit_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                send_button.isEnabled = charSequence.toString().trim { it <= ' ' }.isNotEmpty()

            }

            override fun afterTextChanged(editable: Editable) {}
        })

        // Send button sends a message and clears the EditText
        send_button.setOnClickListener {
            viewModel.sendMessage(message_edit_text.text.toString(), username, null)
            // Clear input box
            message_edit_text.setText("")
        }

        photo_picker_button.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/jpeg"
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), Constants.RC_PHOTO_PICKER)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            val imageURI = data!!.data!!
            viewModel.sendMessage(null, username, imageURI)
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
                AuthUI.getInstance().signOut(this).addOnSuccessListener { loggedOut() }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onBackPressed() {
        val intent = Intent(this, RoomView::class.java)
        intent.putExtra(Constants.USERNAME_PARAM, username)
        startActivity(intent)
        finish()
    }

    private fun loggedOut() {
        val intent = Intent(this, StartView::class.java)
        startActivity(intent)
        finish()
    }

}
