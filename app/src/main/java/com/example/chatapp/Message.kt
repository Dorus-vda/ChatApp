package com.example.chatapp

import android.util.Log
import java.text.SimpleDateFormat

class Message {
    var message: String? = null
    var senderId: String? = null
    var receiverId: String? = null
    var time: String? = null

    constructor(){}

    constructor(message: String?, senderId: String?, receiverId: String?, time: String?){
        this.message = message
        Log.d("TAG", message.toString())
        this.senderId = senderId
        this.receiverId = receiverId
        this.time = time
    }
}