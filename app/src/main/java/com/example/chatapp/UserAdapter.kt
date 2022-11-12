package com.example.chatapp

import android.content.Context
import android.content.Intent
import android.util.Log
import kotlin.concurrent.schedule
import java.util.Timer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.concurrent.timer


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
                    if (snapshot.value.toString() == "False") {
                        holder.onlineled.visibility = View.INVISIBLE // set online icon to invisible
                    }
                    else {
                        holder.onlineled.visibility = View.VISIBLE
                    }
                } else { // else show online icon
                    holder.onlineled.visibility = View.VISIBLE // set online icon to visible
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })


        holder.itemView.setOnClickListener{ // Click registration of send button
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
        val messagecontent = itemView.findViewById<TextView>(R.id.message_content)
        val onlineled = itemView.findViewById<ImageView>(R.id.onlineled)
    }

}