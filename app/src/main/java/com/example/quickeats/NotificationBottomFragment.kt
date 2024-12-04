package com.example.quickeats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quickeats.adapter.NotificationAdapter
import com.example.quickeats.databinding.FragmentNotificationBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NotificationBottomFragment : BottomSheetDialogFragment() {
    private var _binding : FragmentNotificationBottomBinding? = null
    private val binding get() = _binding!!
    private lateinit var notificationAdapter: NotificationAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationBottomBinding.inflate(layoutInflater,container,false)

        val notification = listOf("Your order has been Canceled Successfully","Order has been taken by the driver","Congrats Your Order Placed")
         val notificationImages = listOf(R.drawable.notification0,R.drawable.notification2,R.drawable.notification3)

        notificationAdapter = NotificationAdapter(ArrayList(notification), ArrayList(notificationImages))

        binding.notificationRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = notificationAdapter
        }
        return binding.root
    }

}