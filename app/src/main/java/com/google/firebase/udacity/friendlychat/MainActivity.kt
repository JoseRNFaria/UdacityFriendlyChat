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

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.udacity.friendlychat.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 1
    private var username: String? = null

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private var eventListener: ChildEventListener? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        username = Constants.ANONYMOUS

        firebaseDatabase = FirebaseDatabase.getInstance()

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = firebaseDatabase.reference.child("messages")

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
            val message = FriendlyMessage(message_edit_text.text.toString(), username!!, null)

            databaseReference.push().setValue(message)

            // Clear input box
            message_edit_text.setText("")
        }



        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                onSignedIn(user.displayName)
            } else {
                onSignOut()
                val providers = arrayListOf(
                        AuthUI.IdpConfig.EmailBuilder().build(),
                        AuthUI.IdpConfig.GoogleBuilder().build())


                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .setIsSmartLockEnabled(false)
                                .build(), RC_SIGN_IN)
            }
        }
    }

    private fun onSignOut() {
        username = Constants.ANONYMOUS
        messageAdapter.clear()

        removeChildEventListener()
    }

    private fun onSignedIn(displayName: String?) {
        username = displayName
        addChildEventListener()
    }

    private fun removeChildEventListener() {
        if (eventListener != null) {
            databaseReference.removeEventListener(eventListener!!)
            eventListener = null
        }
    }

    private fun addChildEventListener() {
        if (eventListener == null) {
            eventListener = object : ChildEventListener {
                override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
                override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    val message = p0.getValue(FriendlyMessage::class.java)
                    messageAdapter.add(message)
                }

                override fun onChildRemoved(p0: DataSnapshot) {}
                override fun onCancelled(p0: DatabaseError) {}
            }
            databaseReference.addChildEventListener(eventListener!!)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==RC_SIGN_IN)
        {
            if(resultCode==RESULT_OK)
            {
                Toast.makeText(this,"Signed in",Toast.LENGTH_LONG).show()
            }
            else if(resultCode== Activity.RESULT_CANCELED)
            {
                Toast.makeText(this,"Login cancelled",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.sign_out_menu -> AuthUI.getInstance().signOut(this)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    override fun onPause() {
        super.onPause()
        messageAdapter.clear()
        removeChildEventListener()
        firebaseAuth.removeAuthStateListener(authStateListener)
    }
}
