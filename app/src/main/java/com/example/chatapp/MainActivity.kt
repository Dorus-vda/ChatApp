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
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
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


class MainActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var toolbarcontent: TextView
    private lateinit var logoutButton: ImageButton
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()
        lifecycle.addObserver(ApplicationObserver())

        userList = ArrayList()
        adapter = UserAdapter(this, userList)

        userRecyclerView = findViewById(R.id.userRecyclerView)
        toolbarcontent = findViewById(R.id.largeToolbarcontent)
        toolbarcontent.text = "Contacts"
        logoutButton = findViewById(R.id.LogoutButton)

        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter

        preferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)


        mDbRef.child("user").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()
                for(postSnapshot in snapshot.children){

                    val currentUser = postSnapshot.getValue(User::class.java)


                    if(mAuth.currentUser?.uid != currentUser?.uid){
                        userList.add(currentUser!!)

                    }

                }
                adapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        logoutButton.setOnClickListener(){
            showPopup(logoutButton)
        }
    }


    fun showPopup(v: View){
        val editor: SharedPreferences.Editor = preferences.edit()
        val popup = PopupMenu(this, v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu, popup.menu)
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            if (item.itemId == R.id.logout){
                mAuth.signOut()
                val editor: SharedPreferences.Editor = preferences.edit()
                editor.clear()
                editor.apply()

                val intent = Intent(this@MainActivity,Login::class.java)
                finish()
                startActivity(intent)
                true
            }
            if(item.itemId == R.id.profilepic){
                Log.d("MainActivity", "Profilepic has been picked")

                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 0)
            }
            true
        })
        popup.show()
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("MainActivity", "Image picked")
            selectedPhotoUri = data.data
        }
        uploadImageToFirebaseStorage()
    }

    private fun uploadImageToFirebaseStorage(){
        if (selectedPhotoUri == null)return
        val uid = FirebaseAuth.getInstance().uid
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        val uidref = FirebaseDatabase.getInstance().getReference("/user/$uid/profileImageURL")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener{
                Log.d("MainActivity", "Photo is uploaded ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    uidref.setValue(it.toString())
                        .addOnSuccessListener {
                            Log.d("MainActivity", "Profile photo register in realtimedatabase")
                        }
                }
            }




    }

}