package com.example.chatapp

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class contactAdapter(val context: Context, val userList: ArrayList<User>):
    RecyclerView.Adapter<contactAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_contaclist, parent,false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){ // References to items in xml files
        val textName = itemView.findViewById<TextView>(R.id.txt_name)
        val profileLetter = itemView.findViewById<TextView>(R.id.profile)
        val profilePicture = itemView.findViewById<ImageView>(R.id.profilePicture)
        val messagecontent = itemView.findViewById<TextView>(R.id.message_content)
        val onlineled = itemView.findViewById<ImageView>(R.id.onlineled)
    }

}