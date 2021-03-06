package com.google.firebase.udacity.friendlychat.components.chatRooms

import com.google.firebase.udacity.friendlychat.utils.objects.ChatRoomWithKey

interface ChatRoomsInterface {

    fun openRoom(room:ChatRoomWithKey)

    fun roomOptions(room:ChatRoomWithKey)
}