package com.example.chatapp

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class contactAdapter(val context: Context, val userList: ArrayList<User>):
    RecyclerView.Adapter<contactAdapter.UserViewHolder>() {
    val mDbRef = FirebaseDatabase.getInstance().getReference()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_contaclist, parent,false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUserUID = FirebaseAuth.getInstance().uid
        val contactUser = userList[position]
        holder.contactName.text = contactUser.name
        val contactPictureRef = mDbRef.child("user").child(contactUser.uid.toString())
        contactPictureRef.child("profileImageURL").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value != null){
                    Glide.with(context).load(snapshot.value.toString()).override(100,100).centerCrop().into(holder.contactPicture)
                    Log.d("UserAdapter", snapshot.value.toString())
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        holder.acceptButton.setOnClickListener(){
            val ref = FirebaseDatabase.getInstance().reference
            val friendRequestReference = ref.child("friend_requests").child(currentUserUID.toString())
            friendRequestReference.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("contactList", snapshot.value.toString())
                    for (dataSnapshot in snapshot.children){
                        if(dataSnapshot.value == contactUser.uid){
                            dataSnapshot.ref.removeValue()
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })

            val friendsListReference = ref.child("friends_list").child(currentUserUID.toString()).child(contactUser.uid.toString())
            val requesterListReference = ref.child("friends_list").child(contactUser.uid.toString()).child(currentUserUID.toString())
            friendsListReference.setValue(contactUser!!)
            mDbRef.child("user").child(currentUserUID.toString()).addListenerForSingleValueEvent(object :
                ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val currentUser = snapshot.getValue(User::class.java)
                        requesterListReference.setValue(currentUser!!)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
                })


        }

        holder.declineButton.setOnClickListener(){
            val ref = FirebaseDatabase.getInstance().reference
            val friendRequestReference = ref.child("friend_requests").child(currentUserUID.toString())
            friendRequestReference.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("contactList", snapshot.value.toString())
                    for (dataSnapshot in snapshot.children){
                        if(dataSnapshot.value == contactUser.uid){
                            dataSnapshot.ref.removeValue()
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){ // References to items in xml files
        val acceptButton = itemView.findViewById<AppCompatButton>(R.id.btn_accept)
        val declineButton = itemView.findViewById<AppCompatButton>(R.id.btn_decline)
        val contactName = itemView.findViewById<TextView>(R.id.contact_name)
        val contactPicture = itemView.findViewById<ImageView>(R.id.contact_Picture)
    }

}