package com.example.chatapp

import android.util.Log

//constructs a message object with the following variables
class MessageType {
    var message: String? = null
    var senderId: String? = null
    var receiverId: String? = null
    var time: String? = null
    var type: String? = null

    constructor(){}

    constructor(message: String?, senderId: String?, receiverId: String?, time: String?, type: String?){
        this.message = message
        this.senderId = senderId
        this.receiverId = receiverId
        this.time = time
        this.type = type
    }
}