package com.example.quickeats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quickeats.adapter.MenuAdapter
import com.example.quickeats.databinding.FragmentMenuBottomSheetBinding
import com.example.quickeats.model.MenuItems
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MenuBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding : FragmentMenuBottomSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: FirebaseDatabase
    private lateinit var menuItems : MutableList<MenuItems>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMenuBottomSheetBinding.inflate(inflater,container,false)

        retrieveMenuItems()

        binding.imgBtnBackMenu.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    private fun retrieveMenuItems() {
        database = FirebaseDatabase.getInstance()
        val foodRef : DatabaseReference = database.reference.child("menu")
        menuItems = mutableListOf()

        foodRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(foodSnapshot in snapshot.children){
                    val menuItem = foodSnapshot.getValue(MenuItems::class.java)
                    menuItem?.let { menuItems.add(it) }
                }
                //setting data to adapter
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setAdapter() {
        if(menuItems.isNotEmpty()){
            val adapter = MenuAdapter(menuItems,requireContext())
            binding.recyclerViewMenu.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerViewMenu.adapter = adapter
//            Log.d("ITEMS","setAdapter: data set")
        }else {
            Log.d("ITEMS","setAdapter: data not  set")
        }

    }

}