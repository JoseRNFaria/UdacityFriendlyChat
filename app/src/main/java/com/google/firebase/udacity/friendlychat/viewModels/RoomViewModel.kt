package com.google.firebase.udacity.friendlychat.viewModels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.udacity.friendlychat.utils.objects.ChatRoom
import com.google.firebase.udacity.friendlychat.utils.objects.ChatRoomWithKey

class RoomViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val databaseReference = firebaseDatabase.reference.child("rooms")
    var totalListOfRooms = mutableListOf<ChatRoomWithKey>()
    val listOfRooms = MutableLiveData<MutableList<ChatRoomWithKey>>()

    var searching = false
    var cachedSearch = ""

    fun getRooms() {
        listOfRooms.value = mutableListOf()

        val eventListener = object : ChildEventListener {
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val room = p0.getValue(ChatRoom::class.java)
                totalListOfRooms.add(ChatRoomWithKey(p0.key!!, room!!))

                if (searching) {
                    search(cachedSearch)
                } else {
                    listOfRooms.postValue(totalListOfRooms)
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {}
            override fun onCancelled(p0: DatabaseError) {}
        }
        databaseReference.addChildEventListener(eventListener)

    }

    fun addRoom(username: String, roomName: String, roomDescription: String, roomPassword: String) {
        val chatRoom = ChatRoom(username, roomName, roomDescription, roomPassword)
        databaseReference.push().setValue(chatRoom)
    }

    fun search(searchText: String?) {

        searching = if (searchText == null || searchText.isEmpty()) {
            cachedSearch = ""
            listOfRooms.postValue(totalListOfRooms)
            false
        } else {
            cachedSearch = searchText
            val searchedRooms = totalListOfRooms.filter { it.room.title.contains(searchText) || it.room.description.contains(searchText) }
            listOfRooms.postValue(searchedRooms.toMutableList())
            true
        }
    }

}