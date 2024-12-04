package com.example.quickeats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.quickeats.databinding.ActivityPayOutBinding
import com.example.quickeats.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PayOutActivity : AppCompatActivity() {
    private var _binding: ActivityPayOutBinding? = null
    private val binding get() = _binding!!
    private lateinit var name: String
    private lateinit var phone: String
    private lateinit var address: String
    private lateinit var auth : FirebaseAuth
    private lateinit var totalAmount : String
    private lateinit var foodItemName : ArrayList<String>
    private lateinit var foodItemPrice : ArrayList<String>
    private lateinit var foodItemImage : ArrayList<String>
    private lateinit var foodItemDescription : ArrayList<String>
    private lateinit var foodItemIngredients : ArrayList<String>
    private lateinit var foodItemQuantities : ArrayList<Int>
    private lateinit var databaseRef : DatabaseReference
//    private lateinit var userId :String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference

        setUserData()

        //get User details for db
        val intent = intent
        foodItemName = intent.getStringArrayListExtra("foodItemName") as ArrayList<String>
        foodItemPrice = intent.getStringArrayListExtra("foodItemPrice") as ArrayList<String>
        foodItemImage = intent.getStringArrayListExtra("foodItemImage") as ArrayList<String>
        foodItemDescription = intent.getStringArrayListExtra("foodItemDescription") as ArrayList<String>
        foodItemIngredients = intent.getStringArrayListExtra("foodItemIngredients") as ArrayList<String>
        foodItemQuantities = intent.getIntegerArrayListExtra("foodItemQuantities") as ArrayList<Int>


        totalAmount = calculateAmount().toString() + "₹"
        binding.totalAmountPay.isEnabled = false
        binding.totalAmountPay.setText(totalAmount)

        binding.btnPlacedOrder.setOnClickListener {
            name = binding.name.text.toString()
            address = binding.address.text.toString()
            phone = binding.phonePayOut.text.toString()

            if (name.isBlank() && phone.isBlank() && address.isBlank()){
                Toast.makeText(this,"Please enter all the required details",Toast.LENGTH_SHORT).show()
            }else {
                placeOrder()
            }

        }
        binding.btnBackPay.setOnClickListener {
            finish()
        }
    }

    private fun placeOrder() {
       val userId = auth.currentUser?.uid?:""
        val time = System.currentTimeMillis()
        val itemPushKey = databaseRef.child("OrderDetails").push().key
        val orderDetails = OrderDetails(userId,name,foodItemName,foodItemPrice,foodItemImage,foodItemQuantities,address,totalAmount,phone,time,itemPushKey,false,false)
        val orderRef = databaseRef.child("OrderDetails").child(itemPushKey!!)
        orderRef.setValue(orderDetails).addOnSuccessListener {
            val bottomSheetDialog = CongratsBottomSheetFragment()
            bottomSheetDialog.show(supportFragmentManager,"Test")
            removeItemFromCart()
            addOrderToHistory(orderDetails)
        }
    }

    private fun addOrderToHistory(orderDetails: OrderDetails) {
        val userId = auth.currentUser?.uid?:""
        databaseRef.child("user").child(userId).child("BuyHistory").child(orderDetails.itemPushKey!!)
            .setValue(orderDetails).addOnSuccessListener {

            }
    }


    private fun removeItemFromCart() {
        val userId = auth.currentUser?.uid?:""
        val cartItemsRef = databaseRef.child("user").child(userId).child("CartItems")
        cartItemsRef.removeValue()
    }

    private fun calculateAmount(): Int {
        var totalAmount = 0

        try{
            for(i in 0 until foodItemPrice.size){
                var price = foodItemPrice[i]
                val lastChar = price.last()
                val priceIntVal = if(lastChar == '₹'){
                    price.dropLast(1).toInt()
                }else{
                    price.toInt()
                }
                var quantity = foodItemQuantities[i]
                totalAmount += priceIntVal * quantity
            }
        }catch (e: Exception){
            Log.d("Exception",e.toString())
        }

        return totalAmount
    }

    private fun setUserData() {
        val user = auth.currentUser
        if(user != null){
            val userId = user.uid
            val userRef = databaseRef.child("user").child(userId)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    if(snapshot.exists()){
                        val userName = snapshot.child("name").getValue(String::class.java)?:""
                        val userAddress = snapshot.child("address").getValue(String::class.java)?:""
                        val userPhone = snapshot.child("phoneNumber").getValue(String::class.java)?:""

                        binding.apply {
                            name.setText(userName)
                            address.setText(userAddress)
                            phonePayOut.setText(userPhone)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }
}