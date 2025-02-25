package com.project.aerotrack.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.project.aerotrack.R
import com.project.aerotrack.SharedPrefManager
import com.project.aerotrack.databinding.ItemChatBinding
import com.project.aerotrack.models.Chat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatAdapter(
    private var messages: List<Chat>,
    private val context: Context
) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private val sharedPrefManager = SharedPrefManager(context)
    private val currentUser = sharedPrefManager.getUserName() // Get current user's name

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(messages[position], currentUser.toString())
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    class ViewHolder(private val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: Chat, currentUser: String) {
            binding.userMsg.text = chat.message
            binding.userName.text = chat.name
            binding.msgTime.text = convertTimestampTo12HourFormat(chat.time)

            val layoutParams = binding.messageLayout.layoutParams as? ConstraintLayout.LayoutParams
            if (chat.name == currentUser) {
                // If the message is from the current user, align to the right
                layoutParams?.horizontalBias = 1.0f // Align to the right
//                binding.messageLayout.setBackgroundResource(R.drawable.bg_sender)
            } else {
                // If the message is from another user, align to the left
                layoutParams?.horizontalBias = 0.0f // Align to the left
//                binding.messageLayout.setBackgroundResource(R.drawable.bg_receiver)
            }

            binding.messageLayout.layoutParams = layoutParams
        }
        fun convertTimestampTo12HourFormat(timestamp: Long): String {
            val date = Date(timestamp)
            val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
            return format.format(date)
        }
    }
}
