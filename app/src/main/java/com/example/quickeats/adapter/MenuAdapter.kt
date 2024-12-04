package com.example.quickeats.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quickeats.DetailsActivity
import com.example.quickeats.databinding.MenuItemBinding
import com.example.quickeats.model.MenuItems

class MenuAdapter(private val menuItems: List<MenuItems>,
                  private val context: Context,
) :RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

//    private val itemClickListener : OnClickListener ?= null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuAdapter.MenuViewHolder {
        return MenuViewHolder(MenuItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MenuAdapter.MenuViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return menuItems.size
    }
    inner class MenuViewHolder(private val binding : MenuItemBinding) :RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    openDetailsActivity(position)
                }

            }
        }

        private fun openDetailsActivity(position: Int){
            val menuItem = menuItems[position]

            //intent to open details activity and pass data
            val intent = Intent(context,DetailsActivity::class.java).apply {
                putExtra("MenuItemName",menuItem.foodName)
                putExtra("MenuItemImage",menuItem.foodImage)
                putExtra("MenuItemDescription",menuItem.foodDesc)
                putExtra("MenuItemPrice",menuItem.foodPrice)
                putExtra("MenuItemIngredients",menuItem.foodIngredient)
            }
            context.startActivity(intent)
        }

        //set data into recycler view
        fun bind(position: Int){
            val menuItem = menuItems[position]
            binding.apply {
                txtMenuFoodName.text = menuItem.foodName
                txtMenuFoodPrice.text = menuItem.foodPrice
                val uri = Uri.parse(menuItem.foodImage)
                Glide.with(context).load(uri).into(imageMenu)
            }
        }
    }

}

