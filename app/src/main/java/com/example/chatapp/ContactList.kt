package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


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

            // Query the database for a user with the given email address
            val query = usersReference.orderByChild("email").equalTo(recipientEmail)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Check if there is a user with the given email address
                    if (dataSnapshot.exists()) {
                        // User exists, so we can send the friend request

                        // Iterate over the list of users who match the query criteria
                        for (userSnapshot in dataSnapshot.children) {
                            // Get the user data from the snapshot
                            val user = userSnapshot.getValue(User::class.java)

                            // Use the user data to send the friend request
                            if (user != null) {
                                val recipientUserId = user?.uid

                                // Create a reference to the recipient user's friend requests node
                                val friendRequestReference = mDbRef.child("friend_requests").child(recipientUserId.toString())
                                friendRequestReference.push().child("Sender_Id").setValue(currentUserId.toString())
                                    .addOnSuccessListener {
                                        Toast.makeText(this@ContactList, "Request has been sent", Toast.LENGTH_SHORT).show()
                                        // Friend request sent successfully
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this@ContactList, "Failed to send", Toast.LENGTH_SHORT).show()
                                        // Failed to send friend request
                                    }
                            }
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

            override fun onBackPressed() {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
    }

}


