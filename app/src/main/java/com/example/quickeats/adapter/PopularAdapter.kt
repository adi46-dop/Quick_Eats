package com.example.quickeats.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quickeats.databinding.UserItemListBinding

class PopularAdapter(
    private val userItem: List<String>,
    private val image: List<Int>,
    private val priceList: List<String>
) : RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        return PopularViewHolder(
            UserItemListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return userItem.size
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        val price = priceList[position]
        val item = userItem[position]
        val images = image[position]

        holder.bind(item, images, price)
    }

    class PopularViewHolder(private val binding: UserItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val imagesView = binding.imgFoodPopular
        fun bind(item: String, images: Int, price: String) {
            binding.txtFoodName.text = item
            binding.txtFoodPrice.text = price
            imagesView.setImageResource(images)
        }
    }
}