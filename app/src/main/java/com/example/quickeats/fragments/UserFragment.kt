package com.example.quickeats.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.quickeats.databinding.FragmentUserBinding
import com.example.quickeats.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserFragment : Fragment() {
    private var _binding : FragmentUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUserBinding.inflate(inflater,container,false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        setUserData()

        binding.apply {
            btnSaveUserInfo.visibility = View.INVISIBLE
            nameProfile.isEnabled = false
            addressProfile.isEnabled = false
            emailProfile.isEnabled = false
            phoneProfile.isEnabled = false
        }

        binding.btnEditProfile.setOnClickListener {
           binding.apply {
               btnSaveUserInfo.visibility = View.VISIBLE
               nameProfile.isEnabled = true
               addressProfile.isEnabled = true
               emailProfile.isEnabled = true
               phoneProfile.isEnabled = true
           }
        }

        binding.btnSaveUserInfo.setOnClickListener {
            val name = binding.nameProfile.text.toString()
            val address = binding.addressProfile.text.toString()
            val email = binding.emailProfile.text.toString()
            val phone = binding.phoneProfile.text.toString()

            updateUserInfo(name,address,email,phone)
        }
        return binding.root
    }

    private fun updateUserInfo(name: String, address: String, email: String, phone: String) {
        val userId = auth.currentUser?.uid
        if(userId!= null){
            val userRef = database.getReference("user").child(userId)

            val userData = hashMapOf(
                "name" to name,
                "address" to address,
                "email" to email,
                "phoneNumber" to phone
            )

            userRef.setValue(userData).addOnSuccessListener {
                Toast.makeText(requireContext(),"Profile updated successfully",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(requireContext(),"Profile updated Failed",Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun setUserData() {
        val userId = auth.currentUser?.uid
        if(userId!= null){
            val userReference = database.getReference("user").child(userId)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val userProfile = snapshot.getValue(UserModel::class.java)
                        if(userProfile !=null){
                            binding.nameProfile.setText(userProfile.name)
                            binding.emailProfile.setText(userProfile.email)
                            binding.addressProfile.setText(userProfile.address)
                            binding.phoneProfile.setText(userProfile.phoneNumber)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show()
                }

            })
        }
    }

}