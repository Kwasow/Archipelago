package com.github.kwasow.archipelago.activities

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import com.github.kwasow.archipelago.R
import com.github.kwasow.archipelago.databinding.ActivityAddSourceBinding

class AddSourceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddSourceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_Archipelago_App_DarkNotificationBar)
        window.statusBarColor = resources.getColor(R.color.black, theme)
        binding = ActivityAddSourceBinding.inflate(layoutInflater)

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        val items = listOf("PL", "UK", "DE", "DK")
        val adapter = ArrayAdapter(this, R.layout.list_item, items)
        (binding.countrySelect.editText as AutoCompleteTextView)
                .setAdapter(adapter)

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
        binding.photoLeading.setImageResource(R.drawable.ic_ghost)
        binding.title.setText(R.string.something_went_wrong)
    }

    private fun sourceCash() {
        binding.photoLeading.setImageResource(R.drawable.ic_dollar)
        binding.title.setText(R.string.new_cash_source)
    }

    private fun sourceSavings() {
        binding.photoLeading.setImageResource(R.drawable.ic_credit_card)
        binding.title.setText(R.string.new_savings)
    }

    private fun sourceInvestment() {
        binding.photoLeading.setImageResource(R.drawable.ic_percent)
        binding.title.setText(R.string.new_investment)
    }

    private fun sourceStock() {
        binding.photoLeading.setImageResource(R.drawable.ic_graph)
        binding.title.setText(R.string.new_stock)

        binding.amount.visibility = View.GONE
    }
}