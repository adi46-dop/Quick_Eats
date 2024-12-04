package com.example.quickeats.adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quickeats.databinding.CartItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartAdapter(private val cartItemList:MutableList<String>,
                  private val cartImages: MutableList<String>,
                  private val cartItemPrice: MutableList<String>,
                  private val cartDesc: MutableList<String>,
                  private val cartQuantity: MutableList<Int>,
                  private val cartIngredient: MutableList<String>,
                  private val context: Context
) :RecyclerView.Adapter<CartAdapter.CardViewHolder>() {

    private val auth = FirebaseAuth.getInstance()

    init {
        val database = FirebaseDatabase.getInstance()
        val userId = auth.currentUser?.uid ?: ""
        val cartItemNumber = cartItemList.size

        itemQuantities = IntArray(cartItemNumber){1}
        cartItemsRef = database.reference.child("user").child(userId).child("CartItems")

    }

    companion object{
        private var itemQuantities : IntArray = intArrayOf()
        private lateinit var cartItemsRef : DatabaseReference
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CardViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return cartItemList.size
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(position)
    }

    fun getUpdatedItemQuantities(): MutableList<Int> {
        val itemQuantity = mutableListOf<Int>()
        itemQuantity.addAll(cartQuantity)
//        Log.d("ITEM",itemQuantity.toString())
        return itemQuantity
    }
    inner class CardViewHolder(private val binding: CartItemBinding) :RecyclerView.ViewHolder(binding.root) {
        fun  bind(position: Int){
            binding.apply {
                val qunatities = itemQuantities[position]
                txtCartFoodName.text = cartItemList[position]
                txtCartFoodPrice.text = cartItemPrice[position]
                qunatityCartItem.text = qunatities.toString()


                val uriString = cartImages[position]
                val uri = Uri.parse(uriString)
                Glide.with(context).load(uri).into(imgCart)

                btnMinus.setOnClickListener{
                    decreaseQuantity(position)
                }
                btnPlus.setOnClickListener{
                    increaseQuantity(position)
                }
                btnDelete.setOnClickListener {
                    val itemPostion = adapterPosition
                    if(itemPostion != RecyclerView.NO_POSITION){
                        deleteItem(position)
                    }
                }

            }
        }

        private fun decreaseQuantity(position: Int){
            if(itemQuantities[position] > 1){
                itemQuantities[position]--
                cartQuantity[position] = itemQuantities[position]
                binding.qunatityCartItem.text = itemQuantities[position].toString()

            }
        }

        private fun increaseQuantity(position: Int){
            if(itemQuantities[position] < 10){
                itemQuantities[position]++
                cartQuantity[position] = itemQuantities[position]
                binding.qunatityCartItem.text = itemQuantities[position].toString()
            }
        }
    }

    fun deleteItem(position: Int){
        val positionRetrieve = position
        getUniqueKeyAtPosition(positionRetrieve){ uniqueKey ->
            if(uniqueKey != null){
                removeItem(position,uniqueKey)
            }

        }
    }

    private fun removeItem(position: Int, uniqueKey: String) {
        if(uniqueKey != null){
            cartItemsRef.child(uniqueKey).removeValue().addOnSuccessListener {
                cartItemList.removeAt(position)
                cartImages.removeAt(position)
                cartDesc.removeAt(position)
                cartQuantity.removeAt(position)
                cartItemPrice.removeAt(position)
                cartIngredient.removeAt(position)

                //update item Qualities
                itemQuantities = itemQuantities.filterIndexed { index, i -> index != position }.toIntArray()
                notifyItemRemoved(position)
                notifyItemRangeChanged(position,cartItemList.size)
            }.addOnFailureListener {
                Toast.makeText(context,"Failed to Delete",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getUniqueKeyAtPosition(positionRetrieve: Int, onComplete:(String?)-> Unit) {
        cartItemsRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var uniqueKey:String?= null
                //loop for snapshot children
                snapshot.children.forEachIndexed { index, dataSnapshot ->
                    if(index == positionRetrieve){
                        uniqueKey = dataSnapshot.key
                        return@forEachIndexed
                    }
                    onComplete(uniqueKey)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Data fetching failed",Toast.LENGTH_SHORT).show()
            }

        })
    }




}