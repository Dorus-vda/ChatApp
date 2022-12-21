package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.nio.file.Files.exists


class ContactList : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var btnAdd: Button
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: contactAdapter
    private lateinit var toolbarcontent: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contactlist)
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()
        lifecycle.addObserver(ApplicationObserver())

        userList = ArrayList()
        adapter = contactAdapter(this, userList)

        edtEmail = findViewById(R.id.edt_email)
        userRecyclerView = findViewById(R.id.userRecyclerView)
        toolbarcontent = findViewById(R.id.largeToolbarcontent)
        toolbarcontent.text = "Add Contact"

        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter

        edtEmail = findViewById(R.id.edt_email)
        btnAdd = findViewById(R.id.btn_add)


        mDbRef.child("friend_requests").child(FirebaseAuth.getInstance().uid.toString()).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for(postSnapshot in snapshot.children){

                    val requestId = postSnapshot.getValue()
                    mDbRef.child("user").child(requestId.toString()).addListenerForSingleValueEvent(object :
                    ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()){
                                val currentContact = snapshot.getValue(User::class.java)
                                userList.add(currentContact!!)
                                adapter.notifyDataSetChanged()
                            }
                        }
                        override fun onCancelled(error: DatabaseError){

                        }
                    })


                }


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        btnAdd.setOnClickListener {
            val email = edtEmail.text.toString().trimEnd()

            if ((email.isEmpty())) {
                Toast.makeText(this@ContactList, "Please Enter: Email", Toast.LENGTH_SHORT).show()
            }else{
                sendFriendRequest(email);
            }


        }
    }


    private fun sendFriendRequest(recipientEmail: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val currentUserId = FirebaseAuth.getInstance().uid

            val usersReference = mDbRef.child("user")
            if (recipientEmail == currentUser.email) {
                Toast.makeText(this@ContactList, "You cannot send a friend request to yourself", Toast.LENGTH_SHORT).show()
                return
            }

            // Query the database for a user with the given email address
            val query = usersReference.orderByChild("email").equalTo(recipientEmail)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Check if there is a user with the given email address

                    if (dataSnapshot.exists()) {
                        // User exists, so we can send the friend request
                        val recipientUserId = dataSnapshot.children.first().getValue(User::class.java)?.uid
                        val friendRequestReference = mDbRef.child("friend_requests").child(recipientUserId.toString())

                        friendRequestReference.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(friendRequestSnapshot: DataSnapshot) {
                                // Check if a friend request is already pending for the recipient user
                                if (friendRequestSnapshot.child(currentUserId.toString()).exists()) {
                                    Toast.makeText(this@ContactList, "Friend request already sent", Toast.LENGTH_SHORT).show()
                                    return
                                }
                                // Send the friend request
                                friendRequestReference.push().setValue(currentUserId.toString())
                                    .addOnSuccessListener {
                                        Toast.makeText(this@ContactList, "Request has been sent", Toast.LENGTH_SHORT).show()
                                        // Friend request sent successfully
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this@ContactList, "Failed to send", Toast.LENGTH_SHORT).show()
                                        // Failed to send friend request
                                    }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Failed to retrieve friend request data
                            }
                        })

                        // Check the flag before sending a new friend request
                    } else {
                        Toast.makeText(this@ContactList, "User doesn't exist", Toast.LENGTH_SHORT)
                            .show()
                        // Recipient user does
                    }
                }


                    override fun onCancelled(databaseError: DatabaseError) {
                        // Failed to retrieve recipient user data
                    }
                })
            }
        }

                override fun onBackPressed() {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
        }

    }


