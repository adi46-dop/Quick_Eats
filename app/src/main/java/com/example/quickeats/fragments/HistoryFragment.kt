package com.example.quickeats.fragments

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.quickeats.RecentOrderItems
import com.example.quickeats.adapter.BuyAgainAdapter
import com.example.quickeats.databinding.FragmentHistoryBinding
import com.example.quickeats.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HistoryFragment : Fragment() {
    private var _binding : FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var userId:String
    private var listOfOrderItem: MutableList<OrderDetails> = mutableListOf()
    private lateinit var buyAgainAdapter : BuyAgainAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding = FragmentHistoryBinding.inflate(layoutInflater,container,false)
        //initialization
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid?:""
        database = FirebaseDatabase.getInstance()



        //retrieve user order history
        retrieveBuyHistory()

        binding.recentBuyItem.setOnClickListener {
            setItemRecentBuy()
        }
        binding.btnReceived.setOnClickListener {
            updateOrderStatus()
        }
        return binding.root
    }

    private fun updateOrderStatus() {
        val itemPushKey = listOfOrderItem[0].itemPushKey
        val completeOrderRef = database.reference.child("CompletedOrder").child(itemPushKey!!)

        completeOrderRef.child("paymentReceived").setValue(true)
    }

    //function to see recent items buy
    private fun setItemRecentBuy() {
        listOfOrderItem.firstOrNull()?.let { recentBuy ->
            val intent = Intent(requireContext(),RecentOrderItems::class.java)
            intent.putExtra("RecentBuyOrders",listOfOrderItem as ArrayList<OrderDetails> )
            startActivity(intent)
        }
    }

    private fun  setDataInRecentBuyItem() {
        binding.recentBuyItem.visibility = View.VISIBLE
        val recentOrderItem = listOfOrderItem.firstOrNull()
        recentOrderItem?.let {
            with(binding){
                txtBuyFoodName.text = it.foodNames?.firstOrNull()?:""
                txtBuyPrice.text = it.foodPrice?.firstOrNull()?:""
                val image = it.foodImages?.firstOrNull()?:""
                val uri = Uri.parse(image)
                Glide.with(requireContext()).load(uri).into(imgBuyImage)

                val isOrderIsAccepted = listOfOrderItem[0].orderAccepted
                if(isOrderIsAccepted){
                    orderStatus.background.setTint(Color.GREEN)
                    btnReceived.visibility = View.VISIBLE
                }
            }
        }
    }

    //function to set up the recyclerView with previous order details
    private fun setPreviousBuyItemsRecyclerView() {
        val buyAgainFoodName = mutableListOf<String>()
        val buyAgainFoodPrice = mutableListOf<String>()
        val buyAgainFoodImage = mutableListOf<String>()

        for(i in 1 until listOfOrderItem.size){
            listOfOrderItem[i].foodNames?.firstOrNull()?.let { buyAgainFoodName.add(it) }
            listOfOrderItem[i].foodImages?.firstOrNull()?.let { buyAgainFoodImage.add(it) }
            listOfOrderItem[i].foodPrice?.firstOrNull()?.let { buyAgainFoodPrice.add(it) }
        }

        val recyclerView = binding.buyRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        buyAgainAdapter  = BuyAgainAdapter(buyAgainFoodName,buyAgainFoodPrice, buyAgainFoodImage,requireContext())
        recyclerView.adapter = buyAgainAdapter
    }

    //function to retrieve recent buy items
    private fun retrieveBuyHistory() {
        binding.recentBuyItem.visibility = View.INVISIBLE
        val databaseReference = database.reference.child("user").child(userId).child("BuyHistory")

        val sortQuery = databaseReference.orderByChild("currentTime")

        sortQuery.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(buySnapshot in snapshot.children){
                    val buyHistoryItem = buySnapshot.getValue(OrderDetails::class.java)
                    buyHistoryItem?.let {
                        listOfOrderItem.add(it)
                    }
                }
                listOfOrderItem.reverse()
                if (listOfOrderItem.isNotEmpty()){
                    //display the most recent order details
                    setDataInRecentBuyItem()
                    setPreviousBuyItemsRecyclerView()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to retrieve data", Toast.LENGTH_SHORT).show()
            }

        })
    }


}