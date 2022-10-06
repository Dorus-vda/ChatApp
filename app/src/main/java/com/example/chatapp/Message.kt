package com.example.chatapp

import android.util.Log

class Message {
    var message: String? = null
    var senderId: String? = null
    var receiverId: String? = null

    constructor(){}

    constructor(message: String?, senderId: String?, receiverId: String?){
        this.message = message
        Log.d("TAG", message.toString())
        this.senderId = senderId
        this.receiverId = receiverId
    }
}