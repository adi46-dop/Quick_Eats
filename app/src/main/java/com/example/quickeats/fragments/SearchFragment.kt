package com.example.quickeats.fragments

import android.os.Bundle
import android.view.LayoutInflater
import java.util.ArrayList
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quickeats.adapter.MenuAdapter
import com.example.quickeats.databinding.FragmentSearchBinding
import com.example.quickeats.model.MenuItems
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchFragment : BottomSheetDialogFragment(){
    private var _binding : FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MenuAdapter
    private lateinit var database: FirebaseDatabase
    private val originalMenuItems = mutableListOf<MenuItems>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater,container,false)
        retrieveMenuItem()
        setUpSearchView()

        return binding.root
    }

    private fun retrieveMenuItem() {
        database = FirebaseDatabase.getInstance()

        val foodRef = database.reference.child("menu")
        foodRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for( foodSnapshot in snapshot.children){
                    val menuItem = foodSnapshot.getValue(MenuItems::class.java)

                    menuItem?.let {
                        originalMenuItems.add(it)
                    }
                }
                showAllMenu()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to fetch data...", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun showAllMenu() {
        val filterMenuItem = ArrayList(originalMenuItems)
        setAdapter(filterMenuItem)
    }

    private fun setAdapter(filterMenuItem: List<MenuItems>) {
        adapter = MenuAdapter(filterMenuItem,requireContext())
        binding.recyclerViewOriginalMenu.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewOriginalMenu.adapter = adapter
    }


    private fun setUpSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                filterMenuItem(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterMenuItem(newText)
                return true
            }

        })
    }

    private fun filterMenuItem(query: String) {
        val filterMenuItem = originalMenuItems.filter{
            it.foodName?.contains(query,ignoreCase = true) == true
        }
        setAdapter(filterMenuItem)
        adapter.notifyDataSetChanged()
    }

}