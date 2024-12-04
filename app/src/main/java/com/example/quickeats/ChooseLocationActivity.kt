package com.example.quickeats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.example.quickeats.databinding.ActivityChooseLocationBinding

class ChooseLocationActivity : AppCompatActivity() {
    private var _binding : ActivityChooseLocationBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityChooseLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val locationList: Array<String> = arrayOf("jaipur","Nagpur","Pauni","Bhiwapur","Umred")
        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,locationList)

        val autoCompleteTextView = binding.listLocation
        autoCompleteTextView.setAdapter(adapter)

    }
}