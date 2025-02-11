package com.swavlambancard.udid.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.swavlambancard.udid.databinding.ActivityUserDataBinding

class UserDataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get JSON string from intent
        val jsonData = intent.getStringExtra("jsonData")

        // Display JSON data
        binding.tvUserData.text = jsonData
    }
}
