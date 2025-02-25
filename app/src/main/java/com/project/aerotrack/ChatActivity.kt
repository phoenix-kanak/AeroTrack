package com.project.aerotrack

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.aerotrack.adapters.ChatAdapter
import com.project.aerotrack.databinding.ActivityChatBinding
import com.project.aerotrack.models.Chat

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatRv: RecyclerView
    private lateinit var messageList: MutableList<Chat>
    private lateinit var sharedPref: SharedPrefManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = SharedPrefManager(this)
        val username = sharedPref.getUserName().toString()
        Log.d("messageSent", "$username")
        messageList = mutableListOf()
//        messageList = listOf(Chat("user1", "heljhvyfyffffffffffffffffffsurrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrlo", ""), Chat("user2", "hii", "12:00"))
        chatAdapter = ChatAdapter(messageList , this)
        chatRv = binding.ChatRecycleView
        chatRv.layoutManager = LinearLayoutManager(this)
        chatRv.scrollToPosition(messageList.size - 1)
        binding.ChatRecycleView.adapter = chatAdapter
        fetchMessages(messageList ,chatAdapter )
        binding.sendButton.setOnClickListener {
            val message = binding.MessageEditText.text.toString()

            if (message == "") {
                Toast.makeText(this, "Please write a message", Toast.LENGTH_SHORT).show()
            } else {
                sendMessage(username, message)
            }
        }

    }

    fun sendMessage(username: String, message: String) {
        val database = FirebaseDatabase.getInstance().reference
        val timestamp = System.currentTimeMillis()

        val chatMessage = Chat(username, message, timestamp)
        val messageRef = database.child("messages").push()

        messageRef.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d("messageSent", "success")
                binding.MessageEditText.text = null
            }
            .addOnFailureListener {
                Log.d("messageSent", "failure")
            }
    }

    fun fetchMessages(messageList: MutableList<Chat>, adapter: ChatAdapter) {
        val database = FirebaseDatabase.getInstance().reference.child("messages")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear() // Clear the old messages before updating
                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue(Chat::class.java)
                    if (message != null) {
                        messageList.add(message)
                    }
                }
                adapter.notifyDataSetChanged()
                chatRv.scrollToPosition(messageList.size - 1)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching messages: ${error.message}")
            }
        })
    }

}