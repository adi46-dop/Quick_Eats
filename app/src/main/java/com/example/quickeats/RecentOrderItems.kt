package com.example.quickeats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quickeats.adapter.RecentBuyAdapter
import com.example.quickeats.databinding.ActivityRecentOrderItemsBinding
import com.example.quickeats.model.OrderDetails

class RecentOrderItems : AppCompatActivity() {
    private var _binding: ActivityRecentOrderItemsBinding? = null
    private val binding get() = _binding!!
    private lateinit var foodNames: ArrayList<String>
    private lateinit var foodImages: ArrayList<String>
    private lateinit var foodPrices: ArrayList<String>
    private lateinit var foodQuantities: ArrayList<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRecentOrderItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgBackBuy.setOnClickListener {
            finish()
        }

        val recentOrderItems = intent.getSerializableExtra("RecentBuyOrders") as ArrayList<OrderDetails>
        recentOrderItems.let { orderDetails ->
            if (orderDetails.isNotEmpty()) {
                val recentOrderItem = orderDetails[0]

                foodNames = recentOrderItem.foodNames as ArrayList<String>
                foodImages = recentOrderItem.foodImages as ArrayList<String>
                foodPrices = recentOrderItem.foodPrice as ArrayList<String>
                foodQuantities = recentOrderItem.foodQuantities as ArrayList<Int>

            }
            setAdapter()
        }
    }

    private fun setAdapter() {
        val recyclerV = binding.recyclerViewRecentOrders
        recyclerV.layoutManager = LinearLayoutManager(this)
        val adapter = RecentBuyAdapter(this,foodNames,foodImages,foodPrices,foodQuantities)
        recyclerV.adapter = adapter

    }
}