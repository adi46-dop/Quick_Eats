package com.example.quickeats

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.quickeats.databinding.ActivityDetailsBinding
import com.example.quickeats.model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DetailsActivity : AppCompatActivity() {
    private var _binding : ActivityDetailsBinding? = null
    private val binding get() = _binding!!
    private  var foodName: String? = null
    private  var foodImage: String? = null
    private  var foodDescription: String? = null
    private  var foodIngredients: String? = null
    private  var foodPrice: String? = null

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding =ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.imgBackBuy.setOnClickListener {
            finish()
        }

        foodName = intent.getStringExtra("MenuItemName")
        foodDescription = intent.getStringExtra("MenuItemDescription")
        foodPrice = intent.getStringExtra("MenuItemPrice")
        foodIngredients = intent.getStringExtra("MenuItemIngredients")
        foodImage = intent.getStringExtra("MenuItemImage")

        with(binding){
            txtFoodNameDetails.text = foodName
            txtShortDescriptionDetails.text= foodDescription
            txtIngredientsDetails.text = foodIngredients
            Glide.with(this@DetailsActivity).load(Uri.parse(foodImage)).into(imgFoodDetails)

        }

        binding.btnAddToCartDetails.setOnClickListener {
            addItemToCart()
        }

    }

    private fun addItemToCart() {
        val database = FirebaseDatabase.getInstance().reference
        val userId = auth.currentUser?.uid?:""

        //create a cartItem object
        val cartItem = CartItems(foodName.toString(),foodImage.toString(),foodPrice.toString(),foodDescription.toString(),1,foodIngredients.toString())

        //sava data to firebase database
        database.child("user").child(userId).child("CartItems").push().setValue(cartItem).addOnSuccessListener {
            Toast.makeText(this,"Items added into cart successfully",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this,"Item Not added",Toast.LENGTH_SHORT).show()
        }
    }
}