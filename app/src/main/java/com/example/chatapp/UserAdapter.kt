package com.example.chatapp

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.io.InputStream
import kotlinx.coroutines.*


class UserAdapter(val context: Context, val userList: ArrayList<User>):
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent,false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.textName.text = currentUser.name
        holder.profileLetter.text = currentUser.name.toString().take(1).capitalize() // Set profile letter to first letter of username, capitalized
        val senderUid = FirebaseAuth.getInstance().uid
        val receiverUid = currentUser.uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$senderUid/$receiverUid") // Database reference to latest messages from the contacts of current user
        ref.child("message").addValueEventListener(object : ValueEventListener { // Get latest message from certain user from database
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value.toString() != "null"){ // check if there is a last message
                    holder.messagecontent.text = snapshot.value.toString() // Set latest_message_text to latest message retrieved from database
                } else { // else leave field empty
                    holder.messagecontent.text = ""
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
        val ref2 = FirebaseDatabase.getInstance().getReference("/user/$receiverUid") // Database reference to online status of sender
        ref2.child("online").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value.toString() != "True"){ // check if person is not online
                    if (ref2.child("online").key != "True")
                        holder.onlineled.visibility = View.INVISIBLE // set online icon to invisible
                } else { // else show online icon
                    holder.onlineled.visibility = View.VISIBLE // set online icon to visible
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
        val ref3 = FirebaseDatabase.getInstance().getReference("/user/$receiverUid")
        ref3.child("profileImageURL").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value != null){
                    holder.profileLetter.visibility = View.INVISIBLE
                    Glide.with(context).load(snapshot.value.toString()).override(100,100).centerCrop().into(holder.profilePicture)
                    Log.d("UserAdapter", snapshot.value.toString())
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        holder.itemView.setOnClickListener{
            val intent = Intent(context,ChatActivity::class.java)

            intent.putExtra("name", currentUser.name)
            intent.putExtra("uid", currentUser.uid)

            context.startActivities(arrayOf(intent))
        }

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