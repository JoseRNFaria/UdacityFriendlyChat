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
import com.google.firebase.database.*
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.udacity.friendlychat.BuildConfig
import com.google.firebase.udacity.friendlychat.R
import com.google.firebase.udacity.friendlychat.components.chatMessages.MessageChatAdapter
import com.google.firebase.udacity.friendlychat.utils.Constants
import com.google.firebase.udacity.friendlychat.utils.objects.FriendlyMessage
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*

class ChatView : AppCompatActivity() {

    private val RC_PHOTO_PICKER = 2

    private var username = Constants.ANONYMOUS

    private lateinit var messageChatAdapter: MessageChatAdapter
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private var eventListener: ChildEventListener? = null
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var firebaseRemoteConfig: FirebaseRemoteConfig
    private lateinit var chatPhotosStorageReference: StorageReference


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

        title=roomNameExtra



        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("roomsContent").child(roomKeyExtra)
        firebaseStorage = FirebaseStorage.getInstance()
        chatPhotosStorageReference = firebaseStorage.reference.child("chat_photos")
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(BuildConfig.DEBUG).build()
        firebaseRemoteConfig.setConfigSettings(configSettings)

        val defaultConfigSettings = mapOf(Constants.MSG_LENGTH_KEY to Constants.DEFAULT_MSG_LENGTH_LIMIT)
        firebaseRemoteConfig.setDefaults(defaultConfigSettings)
        fetchConfig()

        // Initialize message ListView and its adapter
        val friendlyMessages = ArrayList<FriendlyMessage>()
        messageChatAdapter = MessageChatAdapter(this, R.layout.item_message, friendlyMessages)
        message_list_view.adapter = messageChatAdapter

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
            val message = FriendlyMessage(message_edit_text.text.toString(), username, null)

            databaseReference.push().setValue(message)

            // Clear input box
            message_edit_text.setText("")
        }





        photo_picker_button.setOnClickListener { _ ->
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/jpeg"
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER)
        }


        addChildEventListener()
    }

    /*private fun onSignOut() {
        username = Constants.ANONYMOUS
        messageChatAdapter.clear()

        removeChildEventListener()
    }

    private fun onSignedIn(displayName: String) {
        username = displayName
        addChildEventListener()
    }*/

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
                    messageChatAdapter.add(message)
                }

                override fun onChildRemoved(p0: DataSnapshot) {}
                override fun onCancelled(p0: DatabaseError) {}
            }
            databaseReference.addChildEventListener(eventListener!!)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            val imageURI = data!!.data!!
            val photoRef = chatPhotosStorageReference.child(imageURI.lastPathSegment!!)
            photoRef.putFile(imageURI).addOnSuccessListener { taskSnapshot ->
                photoRef.downloadUrl.addOnCompleteListener { result ->
                    val url = result.result
                    val friendlyMessage = FriendlyMessage(null, username, url.toString())
                    Toast.makeText(this, url.toString(), Toast.LENGTH_LONG).show()
                    //https://firebasestorage.googleapis.com/v0/b/friendlychat-7c1af.appspot.com/o/chat_photos%2Fimage%3A16002?alt=media&token=b59a3bbb-fc03-4780-9e69-2e02daba3e55
                    databaseReference.push().setValue(friendlyMessage)
                }


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
                AuthUI.getInstance().signOut(this).addOnSuccessListener {loggedOut()}
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        messageChatAdapter.clear()
        removeChildEventListener()
    }

    private fun fetchConfig() {
        var cacheExpiration = 3600L

        if (firebaseRemoteConfig.info.configSettings.isDeveloperModeEnabled) {
            cacheExpiration = 0L
        }

        firebaseRemoteConfig.fetch(cacheExpiration).addOnSuccessListener {
            firebaseRemoteConfig.activateFetched()
            applyRetrievedlenghtLimit()
        }.addOnFailureListener {
            applyRetrievedlenghtLimit()
        }
    }

    fun applyRetrievedlenghtLimit() {
        val msgLength = firebaseRemoteConfig.getLong(Constants.MSG_LENGTH_KEY)
        message_edit_text.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(msgLength.toInt()))
    }

    override fun onBackPressed() {
        val intent = Intent(this, RoomView::class.java)
        intent.putExtra(Constants.USERNAME_PARAM,username)
        startActivity(intent)
        finish()
    }

     private fun loggedOut() {
        val intent = Intent(this, StartView::class.java)
        startActivity(intent)
        finish()
    }

}
