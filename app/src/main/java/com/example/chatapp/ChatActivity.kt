package com.example.chatapp

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.toolbar.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var cameraButton: ImageView
    private lateinit var toolbarContent: TextView
    private lateinit var toolbarImageContent: ImageView
    private lateinit var messageAdapter: messageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var adapter: UserAdapter

    var imageFilePath: String? = null
    var receiverRoom: String? = null
    var senderRoom: String? = null
    var receiverUid: String? = null
    var mDbRef: DatabaseReference = FirebaseDatabase.getInstance("https://metischat-default-rtdb.europe-west1.firebasedatabase.app").getReference()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val name = intent.getStringExtra("name")
        receiverUid = intent.getStringExtra("uid")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid

        lifecycle.addObserver(ApplicationObserver())

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        toolbarContent = findViewById(R.id.largeToolbarcontent)
        toolbarContent.text = name
        toolbarImageContent = findViewById(R.id.toolbarImage)


        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sentButton)
        cameraButton = findViewById(R.id.cameraIcon)
        messageList = ArrayList()
        messageAdapter = messageAdapter(this, messageList)

        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter


        mDbRef.child("user").child(receiverUid.toString()).child("profileImageURL").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Glide.with(this@ChatActivity).load(snapshot.value.toString()).override(80,80).centerCrop().into(toolbarImageContent)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        mDbRef.child("chats").child(senderRoom!!).child("messages")
                .addValueEventListener(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val service = CounterNotificationService(applicationContext)
                        messageList.clear()

                        for(postSnapshot in snapshot.children){
                            val message = postSnapshot.getValue(Message::class.java)
                            messageList.add(message!!)
                            chatRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
                        }
                        messageAdapter.notifyDataSetChanged()

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })

        cameraButton.setOnClickListener(){
            openCameraIntent()
        }


        sendButton.setOnClickListener(){
            val message = messageBox.text.toString()
            val format = SimpleDateFormat("HH:mm")
            val time = format.format(Date())
            val type = "text"

            val messageObject = Message(message, senderUid, receiverUid, time, type)
            val sentMessageObject = Message("You: " + message, senderUid, receiverUid, time, type)

            val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$senderUid/$receiverUid")
            latestMessageRef.setValue(sentMessageObject)
            val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$receiverUid/$senderUid")
            latestMessageToRef.setValue(messageObject)


            if (message != "") {
                mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                    .setValue(messageObject).addOnSuccessListener {
                        mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                            .setValue(messageObject)
                        chatRecyclerView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                    }
            }
            messageBox.setText("")
        }

    }

    var imageLocationUri: Uri? = null

    fun openCameraIntent(){
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
        val imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        imageLocationUri = imageUri
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        if (takePictureIntent.resolveActivity(packageManager) != null){
            startActivityForResult(takePictureIntent, 0)
        }
    }

    var cameraPhotoURI: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == RESULT_OK) {
            cameraPhotoURI = imageLocationUri
            Toast.makeText(this@ChatActivity, cameraPhotoURI.toString(), Toast.LENGTH_SHORT).show()
            uploadImageToFirebaseStorage()
        }
    }

    private fun uploadImageToFirebaseStorage(){
        if (cameraPhotoURI == null)return
        val format = SimpleDateFormat("HH:mm")
        val time = format.format(Date())
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val type = "image"
        val filename = UUID.randomUUID().toString()
        val senderRef = FirebaseDatabase.getInstance().getReference("/chats/$senderRoom/messages")
        val receiverRef =  FirebaseDatabase.getInstance().getReference("/chats/$receiverRoom/messages")
        val ref = FirebaseStorage.getInstance().getReference("/images/messageimages/$uid/$filename")

        ref.putFile(cameraPhotoURI!!)
            .addOnSuccessListener {
                Log.d("MainActivity", "Photo is uploaded ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    val messageObject = Message(it.toString(), FirebaseAuth.getInstance().currentUser?.uid, receiverUid, time, type)
                    senderRef.push()
                        .setValue(messageObject).addOnSuccessListener {
                            receiverRef.push()
                                .setValue(messageObject)
                            chatRecyclerView.smoothScrollToPosition(messageAdapter.itemCount - 1)
                        }
                }
            }

    }

   /* fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.getContentResolver(),
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }*/


}