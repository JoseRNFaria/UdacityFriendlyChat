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
package com.google.firebase.udacity.friendlychat

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import com.google.firebase.database.*
import com.google.firebase.udacity.friendlychat.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private var messageAdapter: MessageAdapter? = null
    private var username: String? = null

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        username = Constants.ANONYMOUS

        firebaseDatabase= FirebaseDatabase.getInstance()
        databaseReference=firebaseDatabase.reference.child("messages")

        // Initialize message ListView and its adapter
        val friendlyMessages = ArrayList<FriendlyMessage>()
        messageAdapter = MessageAdapter(this, R.layout.item_message, friendlyMessages)
        message_list_view.adapter = messageAdapter

        // Initialize progress bar
        progress_bar.visibility = ProgressBar.INVISIBLE

        // ImagePickerButton shows an image picker to upload a image for a message
        photo_picker_button.setOnClickListener {
            // TODO: Fire an intent to show an image picker
        }

        // Enable Send button when there's text to send
        message_edit_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                send_button.isEnabled = charSequence.toString().trim { it <= ' ' }.isNotEmpty()

            }

            override fun afterTextChanged(editable: Editable) {}
        })
        message_edit_text.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(Constants.DEFAULT_MSG_LENGTH_LIMIT))

        // Send button sends a message and clears the EditText
        send_button.setOnClickListener {
            val message=FriendlyMessage(message_edit_text.text.toString(),username!!,null)

            databaseReference.push().setValue(message)
            // TODO: Send messages on click

            // Clear input box
            message_edit_text.setText("")
        }

        databaseReference.addChildEventListener(object : ChildEventListener{
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }


}
