package com.github.kwasow.archipelago.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.kwasow.archipelago.databinding.ActivityAddSourceBinding

class AddSourceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddSourceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddSourceBinding.inflate(layoutInflater)

        // Check which source details should be rendered
        when (intent.getIntExtra("sourceType", 0)) {
            0 -> somethingWentWrong()
            1 -> sourceCash()
            2 -> sourceSavings()
            3 -> sourceInvestment()
            4 -> sourceStock()
        }

        setContentView(binding.root)
    }

    private fun somethingWentWrong() {

    }

    private fun sourceCash() {

    }

    private fun sourceSavings() {

    }

    private fun sourceInvestment() {

    }

    private fun sourceStock() {

    }
}