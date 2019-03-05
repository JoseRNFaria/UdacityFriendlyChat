package com.google.firebase.udacity.friendlychat.viewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.net.Uri
import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.udacity.friendlychat.BuildConfig
import com.google.firebase.udacity.friendlychat.utils.Constants
import com.google.firebase.udacity.friendlychat.utils.objects.FriendlyMessage

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private var firebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var databaseReference: DatabaseReference
    private var firebaseStorage = FirebaseStorage.getInstance()
    private var chatPhotosStorageReference = firebaseStorage.reference.child("chat_photos")
    private var firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    var msgLength = MutableLiveData<Int>()

    val allMessages = mutableListOf<FriendlyMessage>()
    val chatMessages = MutableLiveData<MutableList<FriendlyMessage>>()

    init {
        msgLength.value = Constants.DEFAULT_MSG_LENGTH_LIMIT
        val configSettings = FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(BuildConfig.DEBUG).build()
        firebaseRemoteConfig.setConfigSettings(configSettings)
        val defaultConfigSettings = mapOf(Constants.MSG_LENGTH_KEY to Constants.DEFAULT_MSG_LENGTH_LIMIT)
        firebaseRemoteConfig.setDefaults(defaultConfigSettings)
        fetchConfig()
    }

    fun getMessages(roomKey: String) {
        chatMessages.value = mutableListOf()
        databaseReference = firebaseDatabase.reference.child("roomsContent").child(roomKey)

        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                Log.d("messages1", p1 ?: "")
                val message = p0.getValue(FriendlyMessage::class.java)
                allMessages.add(message!!)
                chatMessages.postValue(allMessages)
            }

            override fun onChildRemoved(p0: DataSnapshot) {}
            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    fun sendMessage(message: String?, username: String, imageURI: Uri?) {
        if (imageURI == null) {
            databaseReference.push().setValue(FriendlyMessage(message, username, imageURI))
        } else {
            val photoRef = chatPhotosStorageReference.child(imageURI.lastPathSegment!!)
            photoRef.putFile(imageURI).addOnSuccessListener { taskSnapshot ->
                photoRef.downloadUrl.addOnCompleteListener { result ->
                    val url = result.result
                    databaseReference.push().setValue(FriendlyMessage(message, username, url.toString()))
                }
            }
        }
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


    private fun applyRetrievedlenghtLimit() {
        val msgLengthReceived = firebaseRemoteConfig.getLong(Constants.MSG_LENGTH_KEY)
        msgLength.postValue(msgLengthReceived.toInt())
    }

}
