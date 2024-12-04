package com.example.quickeats.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quickeats.databinding.NotificationItemBinding

class NotificationAdapter(private var notification :ArrayList<String>,private val notificationImage : ArrayList<Int>): RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = NotificationItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NotificationViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return notification.size
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(position)
    }


    inner class NotificationViewHolder(private val binding : NotificationItemBinding) :RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int){
            binding.txtNotificationView.text = notification[position]
            binding.imgNotificationView.setImageResource(notificationImage[position])
        }

    }


}

