package com.github.kwasow.archipelago.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.kwasow.archipelago.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        setContentView(binding.root)
    }
}
