package com.example.chatapp

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class UserAdapter(val context: Context, val userList: ArrayList<User>):
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent,false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.textName.text = currentUser.name
        holder.profileLetter.text = currentUser.name.toString().take(1).capitalize()
        val senderUid = FirebaseAuth.getInstance().uid
        val receiverUid = currentUser.uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$senderUid/$receiverUid") // Get latest message from certain user from database
        ref.child("message").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value.toString() != "null"){ // check if there is a last message
                    holder.messagecontent.post(Runnable {
                        val lineCnt: Int = holder.messagecontent.getLineCount()
                        Log.d("TEST", lineCnt.toString())
                        if (lineCnt > 1){
                            holder.threeDots.visibility = View.VISIBLE
                        }else{
                            holder.messagecontent.text = snapshot.value.toString()
                        }
                    })
                } else { // else leave field empty
                    holder.messagecontent.text = ""
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        holder.messagecontent.text = "TEST"

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

    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val textName = itemView.findViewById<TextView>(R.id.txt_name)
        val profileLetter = itemView.findViewById<TextView>(R.id.profile)
        val messagecontent = itemView.findViewById<TextView>(R.id.message_content)
        val threeDots = itemView.findViewById<TextView>(R.id.threedots)
    }

}