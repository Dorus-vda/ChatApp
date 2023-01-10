package com.example.chatapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class messageAdapter(val context: Context, val messageList: ArrayList<MessageType>):

    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVE = 1
    val ITEM_SENT = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if(viewType == 1){
            //inflate receive
            val view: View = LayoutInflater.from(context).inflate(R.layout.receive, parent, false)
            return ReceiveViewHolder(view)
        }else{
            // inflate sent
            val view: View = LayoutInflater.from(context).inflate(R.layout.sent, parent, false)
            return SentViewHolder(view)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        if (holder.javaClass == SentViewHolder::class.java){
            // do the things to sent view holder
            val viewHolder = holder as SentViewHolder
            if (currentMessage.type != "image") {
                holder.sentImage.visibility = View.GONE
                holder.sentMessage.text = currentMessage.message
                holder.sentTime.text = currentMessage.time
                holder.sentMessage.visibility = View.VISIBLE
            } else {
                holder.sentImage.visibility = View.VISIBLE
                Glide.with(context).load(currentMessage.message).override(200, 250).centerCrop().into(holder.sentImage)
                holder.sentMessage.visibility = View.GONE
                holder.sentTime.text = currentMessage.time
            }
        }else{
            //do stuff for receiving
            val viewHolder = holder as ReceiveViewHolder
            if (currentMessage.type != "image"){
                holder.receiveImage.visibility = View.GONE
                holder.receiveMessage.text = currentMessage.message
                holder.receiveTime.text = currentMessage.time
                holder.receiveMessage.visibility = View.VISIBLE
            } else {
                holder.receiveImage.visibility = View.VISIBLE
                Glide.with(context).load(currentMessage.message).override(200, 250).centerCrop().into(holder.receiveImage)
                holder.receiveMessage.visibility = View.GONE
                holder.receiveTime.text = currentMessage.time
            }
        }

        holder.itemView.setOnClickListener(){
            if (currentMessage.type == "image"){
                val intent = Intent(context,PhotoActivity::class.java)

                intent.putExtra("imageURL", currentMessage.message)

                context.startActivities(arrayOf(intent))
            }
        }

    }



    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]

        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
            return ITEM_SENT
        }else {
            return ITEM_RECEIVE
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sentImage = itemView.findViewById<ImageView>(R.id.sentImage)
        val sentMessage = itemView.findViewById<TextView>(R.id.txt_sent_message)
        val sentTime = itemView.findViewById<TextView>(R.id.senttime)
    }

    class ReceiveViewHolder(itemView: View)  : RecyclerView.ViewHolder(itemView){
        val receiveImage = itemView.findViewById<ImageView>(R.id.receiveImage)
        val receiveMessage = itemView.findViewById<TextView>(R.id.txt_receive_message)
        val receiveTime = itemView.findViewById<TextView>(R.id.receivetime)
    }

}