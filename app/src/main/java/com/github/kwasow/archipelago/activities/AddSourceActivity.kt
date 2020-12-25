package com.github.kwasow.archipelago.activities

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
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

        // TODO: Get these from assets
        // This is a placeholder
        val countries = listOf("PL", "UK", "DE", "DK")
        val adapter = ArrayAdapter(this, R.layout.list_item, countries)
        binding.countrySelect.setAdapter(adapter)

        // TODO: Create a list with string resources
        // This is a placeholder
        val caps = listOf("Daily", "Weekly", "Monthly", "Bi-yearly", "Yearly", "End of month")
        val adapter2 = ArrayAdapter(this, R.layout.list_item, caps)
        binding.capitalization.setAdapter(adapter2)

        // Check which source details should be rendered
        when (intent.getIntExtra("sourceType", 0)) {
            0 -> somethingWentWrong()
            1 -> sourceCash()
            2 -> sourceSavings()
            3 -> sourceInvestment()
            4 -> sourceStock()
        }

        binding.amount.setCurrency("zł")

        setContentView(binding.root)
    }

    fun finishAdding(view: View) {
        // TODO: Write class for adding and managing this data
    }

    private fun somethingWentWrong() {
        binding.photoLeading.setImageResource(R.drawable.ic_ghost)
        binding.title.setText(R.string.something_went_wrong)
    }

    private fun sourceCash() {
        binding.photoLeading.setImageResource(R.drawable.ic_dollar)
        binding.title.setText(R.string.new_cash_source)
        binding.interestLayout.visibility = View.GONE
        binding.capitalizationLayout.visibility = View.GONE
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