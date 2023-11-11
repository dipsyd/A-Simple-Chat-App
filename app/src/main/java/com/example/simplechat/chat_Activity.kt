package com.example.simplechat

import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class chat_Activity : AppCompatActivity() {

    private lateinit var chatRecyclle : RecyclerView
    private lateinit var Messagebox : EditText
    private lateinit var sendbutton : ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbref: DatabaseReference

    var receiverRoom: String? = null
    var senderRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val name = intent.getStringExtra("name")

        supportActionBar?.title = name

        chatRecyclle = findViewById(R.id.chatRecycle)
        Messagebox = findViewById(R.id.Messagebox)
        sendbutton = findViewById(R.id.sendbutton)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this,messageList)

        chatRecyclle.layoutManager = LinearLayoutManager(this)
        chatRecyclle.adapter = messageAdapter
        val receiveruid = intent.getStringExtra("uid")

        val senderuid = FirebaseAuth.getInstance().currentUser?.uid
        mDbref = FirebaseDatabase.getInstance().reference

        senderRoom = receiveruid + senderuid
        receiverRoom = senderuid + receiveruid





        //logic for adding data to recycler view
        mDbref.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear()

                    for(postSnapshot in snapshot.children){

                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)

                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

//Adding message to Database
        sendbutton.setOnClickListener {
            val message = Messagebox.text.toString()
            val messageObject = Message(message, senderuid )


            mDbref.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    mDbref.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)
                }
            Messagebox.setText(" ")
        }
    }
}