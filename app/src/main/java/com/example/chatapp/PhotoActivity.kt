package com.example.chatapp

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_photo.*

class PhotoActivity : AppCompatActivity() {

    private lateinit var downloadButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(ApplicationObserver())
        setContentView(R.layout.activity_photo)
        val imageURL = intent.getStringExtra("imageURL")
        Glide.with(this@PhotoActivity).load(imageURL).fitCenter().into(findViewById(R.id.photoHolder))


    }
}