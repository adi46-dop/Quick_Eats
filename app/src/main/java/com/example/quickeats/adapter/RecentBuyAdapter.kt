package com.example.quickeats.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quickeats.databinding.RecentBuyItemBinding

class RecentBuyAdapter(private var context: Context,
                       private var foodNameList:ArrayList<String>,
                       private var foodImageList:ArrayList<String>,
                       private var foodPriceList:ArrayList<String>,
                       private var foodQuantityList:ArrayList<Int>,
    ): RecyclerView.Adapter<RecentBuyAdapter.RecentViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentViewHolder {
        val binding = RecentBuyItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return RecentViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return foodNameList.size
    }

    override fun onBindViewHolder(holder: RecentViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class RecentViewHolder(private val itemBinding: RecentBuyItemBinding):RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(position: Int){
            itemBinding.apply {
                txtFoodNameBuy.text = foodNameList[position]
                txtFoodPriceBuy.text = foodPriceList[position]
                qunaittyBuy.text = foodQuantityList[position].toString()

                val uriString = foodImageList[position]
                val uri = Uri.parse(uriString)
                Glide.with(context).load(uri).into(imgRecentBuy)
            }
        }
    }

}