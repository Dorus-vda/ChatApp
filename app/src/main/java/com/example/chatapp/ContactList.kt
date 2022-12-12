package com.example.chatapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.Constants
import com.google.firebase.storage.FirebaseStorage
import id.zelory.compressor.Compressor
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class ContactList : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var btnAdd: Button
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter
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
        adapter = UserAdapter(this, userList)

        edtEmail = findViewById(R.id.edt_email)
        toolbarcontent = findViewById(R.id.largeToolbarcontent)
        toolbarcontent.text = "Add Contact"

        edtEmail = findViewById(R.id.edt_email)
        btnAdd = findViewById(R.id.btn_add)


        btnAdd.setOnClickListener {
            val email = edtEmail.text.toString()

            if ((email.isEmpty())) {
                Toast.makeText(this@ContactList, "Please Enter: Email", Toast.LENGTH_SHORT).show()
            }else{
                sendFriendRequest(email);
            }


        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun sendFriendRequest(recipientUserId: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val currentUserId = currentUser.uid
            val usersReference = mDbRef.child("user")
            val recipientUserReference = usersReference.child(recipientUserId)

            recipientUserReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val recipientUser = dataSnapshot.getValue(User::class.java)
                    if (recipientUser != null) {
                        // Recipient user exists, so we can send the friend request
                        val friendRequestReference = mDbRef.child("friend_requests").child(recipientUserId)
                        val requestData = HashMap<String, Any>()
                        requestData["sender_id"] = currentUserId
                        requestData["timestamp"] = System.currentTimeMillis()
                        friendRequestReference.setValue(requestData)
                            .addOnSuccessListener {
                                Toast.makeText(this@ContactList, "Request has been sent", Toast.LENGTH_SHORT).show()
                            // Friend request sent successfully
                            }
                            .addOnFailureListener {
                                Toast.makeText(this@ContactList, "Failed to send", Toast.LENGTH_SHORT).show()
                            // Failed to send friend request
                            }
                    } else {
                        Toast.makeText(this@ContactList, "User doesn't exist", Toast.LENGTH_SHORT).show()
                        // Recipient user does not exist, so we cannot send the friend request
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Failed to retrieve recipient user data
                }
            })
        }
    }

}


