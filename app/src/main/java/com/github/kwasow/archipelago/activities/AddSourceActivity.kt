package com.github.kwasow.archipelago.activities

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.github.kwasow.archipelago.R
import com.github.kwasow.archipelago.databinding.ActivityAddSourceBinding
import com.github.kwasow.archipelago.utils.CountryManager
import com.github.kwasow.archipelago.utils.SourceManager

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

        // Set up source chooser
        val sources = listOf("cash source", "savings account", "investment", "stock")
        val sourcesAdapter = ArrayAdapter(this, R.layout.list_item, sources)
        binding.sourceType.setAdapter(sourcesAdapter)
        // Listen to source selected and show the appropriate views
        binding.sourceType.setOnItemClickListener { _, _, i, _ ->
            // Reset to default state in case something was visible that shouldn't be
            binding.finishButton.isEnabled = true
            binding.interestLayout.visibility = View.GONE
            binding.capitalizationLayout.visibility = View.GONE
            binding.dateStartLayout.visibility = View.GONE
            binding.dateEndLayout.visibility = View.GONE

            // Things to always show
            binding.sourceNameLayout.visibility = View.VISIBLE
            binding.countrySelectLayout.visibility = View.VISIBLE
            binding.amountLayout.visibility = View.VISIBLE
            binding.finishButton.visibility = View.VISIBLE

            when (i) {
                // Cash
                0 -> {
                    sourceCash()
                }
                // Savings
                1 -> {
                    sourceSavings()
                }
                // Investment
                2 -> {
                    sourceInvestment()
                }
                // Stock
                3 -> {
                    sourceStock()
                }
            }
        }

        // Set up country chooser
        val countries = CountryManager.getCountries(this)
        val countryCodes = mutableListOf<String>()
        countries.forEach {
            countryCodes.add(it.code)
        }
        val countryAdapter = ArrayAdapter(this, R.layout.list_item, countryCodes)
        binding.countrySelect.setAdapter(countryAdapter)
        // Set currency when country is selected
        binding.countrySelect.setOnItemClickListener { _, _, i, _ ->
            binding.amount.setCurrency(countries[i].currency)
        }

        // Set up possible capitalization options
        val caps = listOf(
                resources.getString(SourceManager.Capitalization.EndOfMonth.value),
                resources.getString(SourceManager.Capitalization.EndOfInvestment.value),
                resources.getString(SourceManager.Capitalization.Monthly.value),
                resources.getString(SourceManager.Capitalization.Yearly.value)
        )
        val capAdapter = ArrayAdapter(this, R.layout.list_item, caps)
        binding.capitalization.setAdapter(capAdapter)

        binding.interest.setCurrency("%")

        setContentView(binding.root)
    }

    fun finishAdding(view: View) {
        // TODO: Get and save data
    }

    private fun sourceCash() {
        // TODO: Maybe add animation when selecting source
        binding.photoLeading.setImageResource(R.drawable.ic_dollar)
    }

    private fun sourceSavings() {
        binding.photoLeading.setImageResource(R.drawable.ic_credit_card)

        binding.interestLayout.visibility = View.VISIBLE
        binding.capitalizationLayout.visibility = View.VISIBLE
    }

    private fun sourceInvestment() {
        binding.photoLeading.setImageResource(R.drawable.ic_percent)

        binding.interestLayout.visibility = View.VISIBLE
        binding.capitalizationLayout.visibility = View.VISIBLE
        binding.dateStartLayout.visibility = View.VISIBLE
        binding.dateEndLayout.visibility = View.VISIBLE
    }

    private fun sourceStock() {
        // TODO: Stocks
        binding.photoLeading.setImageResource(R.drawable.ic_graph)

        binding.finishButton.isEnabled = false
    }
}