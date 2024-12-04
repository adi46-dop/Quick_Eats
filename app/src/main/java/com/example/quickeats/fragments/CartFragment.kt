package com.example.quickeats.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quickeats.PayOutActivity
import com.example.quickeats.adapter.CartAdapter
import com.example.quickeats.databinding.FragmentCartBinding
import com.example.quickeats.model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartFragment : Fragment() {

    private var _binding : FragmentCartBinding? = null
    private val binding get() =  _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var foodName: MutableList<String>
    private lateinit var foodDescription: MutableList<String>
    private lateinit var foodImagesUri: MutableList<String>
    private lateinit var foodIngredients: MutableList<String>
    private lateinit var foodPrice: MutableList<String>
    private lateinit var quntity: MutableList<Int>
    private lateinit var userId: String
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCartBinding.inflate(inflater,container,false)

       auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()



        retrieveCartItems()


        binding.btnProceedCart.setOnClickListener {
            getItemDetails()
//           val intent = Intent(requireContext(), PayOutActivity::class.java)
//            startActivity(intent)
        }



        return (binding.root)
    }

    private fun getItemDetails() {
        userId = auth.currentUser?.uid?:""
        val orderReference = database.reference.child("user").child(userId).child("CartItems")
        val foodQuantities = cartAdapter.getUpdatedItemQuantities()

        val foodName = mutableListOf<String>()
        val foodImagesUri = mutableListOf<String>()
        val foodPrice = mutableListOf<String>()
        val foodDescription = mutableListOf<String>()
        val foodIngredients = mutableListOf<String>()

        orderReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                for(foodSnapshot in snapshot.children){
                    val orderItems = foodSnapshot.getValue(CartItems::class.java)
                    orderItems?.foodName?.let { foodName.add(it) }
                    orderItems?.foodImage?.let { foodImagesUri.add(it) }
                    orderItems?.foodPrice?.let { foodPrice.add(it) }
                    orderItems?.foodDesc?.let { foodDescription.add(it) }
                    orderItems?.foodIngredient?.let { foodIngredients.add(it) }
                }
                orderNow(foodName,foodImagesUri,foodPrice,foodDescription,foodIngredients,foodQuantities)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(),"Failed to get Data",Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun orderNow(
        foodName: MutableList<String>,
        foodImage: MutableList<String>,
        foodPrice: MutableList<String>,
        foodDescription: MutableList<String>,
        foodIngredients: MutableList<String>,
        foodQuantities: MutableList<Int>
    ) {
        if(isAdded && context!=null){
            val intent = Intent(requireContext(),PayOutActivity::class.java)
            intent.putExtra("foodItemName",foodName as ArrayList<String>)
            intent.putExtra("foodItemPrice",foodPrice as ArrayList<String>)
            intent.putExtra("foodItemImage",foodImage as ArrayList<String>)
            intent.putExtra("foodItemDescription",foodDescription as ArrayList<String>)
            intent.putExtra("foodItemIngredients",foodIngredients as ArrayList<String>)
            intent.putExtra("foodItemQuantities",foodQuantities as ArrayList<Int>)
            startActivity(intent)
        }
    }

    private fun retrieveCartItems() {

        userId = auth.currentUser?.uid ?: ""
        val foodRef : DatabaseReference =  database.reference.child("user").child(userId).child("CartItems")

        //list to store  cart items
        foodName = mutableListOf()
        foodImagesUri = mutableListOf()
        foodPrice = mutableListOf()
        foodDescription = mutableListOf()
        quntity = mutableListOf()
        foodIngredients = mutableListOf()

        //fetch data from the database
        foodRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(foodSnapshot in snapshot.children){
                    //getting the cartItems object from the child node
                    val cartItems = foodSnapshot.getValue(CartItems::class.java)

                    //add cart item details to the list
                    cartItems?.foodName?.let { foodName.add(it) }
                    cartItems?.foodImage?.let { foodImagesUri.add(it) }
                    cartItems?.foodPrice?.let { foodPrice.add(it) }
                    cartItems?.foodDesc?.let { foodDescription.add(it) }
                    cartItems?.foodQuantity?.let { quntity.add(it) }
                    cartItems?.foodIngredient?.let { foodIngredients.add(it) }
                }
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Data fetching failed", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setAdapter() {
        val requiredContext = requireContext()
       cartAdapter = CartAdapter(foodName,foodImagesUri,foodPrice,foodDescription,quntity,foodIngredients,requiredContext)
        binding.cartRecyclerview.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.cartRecyclerview.adapter = cartAdapter
    }

}