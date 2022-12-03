package com.example.chatapp

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class imageMessage {
    var imagePath: String? = null
    var senderId: String? = null
    var receiverId: String? = null
    var time: String? = null
    var type: String = "image"

    constructor(){}

    constructor(imagePath: String?, senderId: String?, receiverId: String?, time: String?){
        this.imagePath = imagePath
        Log.d("TAG", imagePath.toString())
        this.senderId = senderId
        this.receiverId = receiverId
        this.time = time
        this.type = type
    }
}