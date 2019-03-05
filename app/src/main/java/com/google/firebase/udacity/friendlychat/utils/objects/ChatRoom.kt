package com.google.firebase.udacity.friendlychat.utils.objects

data class ChatRoom(var user: String="", var title: String="", var description: String = "", var password: String = "")

data class ChatRoomWithKey(var key:String,var room: ChatRoom)