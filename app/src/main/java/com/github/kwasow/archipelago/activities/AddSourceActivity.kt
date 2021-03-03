package com.github.kwasow.archipelago.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.github.kwasow.archipelago.R
import com.github.kwasow.archipelago.data.Capitalization
import com.github.kwasow.archipelago.data.Country
import com.github.kwasow.archipelago.data.SourceAccount
import com.github.kwasow.archipelago.data.SourceCash
import com.github.kwasow.archipelago.data.SourceInvestment
import com.github.kwasow.archipelago.data.Transaction
import com.github.kwasow.archipelago.databinding.ActivityAddSourceBinding
import com.github.kwasow.archipelago.utils.CountryManager
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class AddSourceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddSourceBinding
    private lateinit var countries: Array<Country>
    private lateinit var caps: List<String>
    var currentSource = 0
    var countryChosen = false
    var capChosen = false

    var startDate: Date? = null
    var endDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_Archipelago_App_DarkNotificationBar)
        window.statusBarColor = resources.getColor(R.color.black, theme)
        binding = ActivityAddSourceBinding.inflate(layoutInflater)

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        // TODO: Load from resource strings
        // Set up source picker
        val sources = listOf("cash source", "savings account", "investment")
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
                0 -> sourceCash()
                // Savings
                1 -> sourceSavings()
                // Investment
                2 -> sourceInvestment()
            }
        }

        // Set up country chooser
        countries = CountryManager.getCountries(this)
        val countryCodes = mutableListOf<String>()
        countries.forEach {
            countryCodes.add(it.code)
        }
        val countryAdapter = ArrayAdapter(this, R.layout.list_item, countryCodes)
        binding.countrySelect.setAdapter(countryAdapter)
        // Set currency when country is selected
        binding.countrySelect.setOnItemClickListener { _, _, i, _ ->
            binding.amount.currencyCode = countries[i].currencyCode
            countryChosen = true
        }

        // Set up possible capitalization options
        caps = listOf(
            resources.getString(Capitalization.EndOfMonth.value),
            resources.getString(Capitalization.EndOfInvestment.value),
            resources.getString(Capitalization.Monthly.value),
            resources.getString(Capitalization.Yearly.value)
        )
        val capAdapter = ArrayAdapter(this, R.layout.list_item, caps)
        binding.capitalization.setAdapter(capAdapter)
        binding.capitalization.setOnItemClickListener { _, _, _, _ -> capChosen = true }

        binding.interest.currency = "%"

        setContentView(binding.root)
    }

    fun finishAdding(view: View) {
        // Check if all the needed details were provided
        var error = false
        if (binding.sourceName.text.toString().isBlank()) {
            binding.sourceNameLayout.error = resources.getString(R.string.error_blank)
            error = true
        } else {
            binding.sourceNameLayout.error = null
        }

        if (!countryChosen) {
            binding.countrySelectLayout.error = resources.getString(R.string.error_blank)
            error = true
        } else {
            binding.countrySelectLayout.error = null
        }

        if ((currentSource == 1 || currentSource == 2) && !capChosen) {
            binding.capitalizationLayout.error = resources.getString(R.string.error_blank)
            error = true
        } else {
            binding.capitalizationLayout.error = null
        }

        if (currentSource == 2) {
            if (binding.dateStart.text.toString().isBlank()) {
                binding.dateStartLayout.error = resources.getString(R.string.error_blank)
                error = true
            } else {
                binding.dateStartLayout.error = null
            }

            if (binding.dateEnd.text.toString().isBlank()) {
                binding.dateEndLayout.error = resources.getString(R.string.error_blank)
                error = true
            } else {
                binding.dateEndLayout.error = null
            }

            if (endDate != null && startDate != null) {
                if (endDate!!.before(startDate)) {
                    binding.dateEndLayout.error = resources.getString(R.string.error_endDate_before_startDate)
                    error = true
                } else if (startDate!!.before(endDate)) {
                    binding.dateEndLayout.error = null
                }
            }
        }
        // Don't continue if any errors were found
        if (error) return

        // Get common source attributes
        val name = binding.sourceName.text.toString()
        val countryTmp = CountryManager
            .getCountyByCode(this, binding.countrySelect.text.toString())
        val country = countryTmp.name
        val countryCode = countryTmp.code
        val currency = countryTmp.currencyCode
        val amount = binding.amount.getMoneyValue()

        val transactions = mutableListOf(
            Transaction(
                Date(),
                resources.getString(R.string.initial_account_state),
                amount,
                resources.getString(R.string.added_automatically)
            )
        )

        // Specific stuff (+checking if succeeded)
        when (currentSource) {
            // Cash
            0 -> {
                val source = SourceCash(
                    name,
                    country,
                    countryCode,
                    currency,
                    amount,
                    transactions
                )
                if (!source.save(this)) {
                    // TODO: Error should be more specific in the future
                    // TODO: Maybe add report issue button to this
                    Snackbar.make(
                        binding.root,
                        R.string.something_went_wrong,
                        Snackbar.LENGTH_SHORT
                    ).show()
                    return
                }
            }
            // Savings account
            1 -> {
                val cap = getCapitalization() ?: return
                val source = SourceAccount(
                    name,
                    country,
                    countryCode,
                    currency,
                    amount,
                    (binding.interest.getDoubleValue() * 100).toInt(),
                    cap,
                    transactions
                )
                if (!source.save(this)) {
                    Snackbar.make(
                        binding.root,
                        R.string.something_went_wrong,
                        Snackbar.LENGTH_SHORT
                    ).show()
                    return
                }
            }
            // Investment
            2 -> {
                val cap = getCapitalization() ?: return
                val source = SourceInvestment(
                    name,
                    country,
                    countryCode,
                    currency,
                    amount,
                    (binding.interest.getDoubleValue() * 100).toInt(),
                    cap,
                    // These are not null, because we checked it earlier
                    startDate!!,
                    endDate!!
                )
                if (!source.save(this)) {
                    Snackbar.make(
                        binding.root,
                        R.string.something_went_wrong,
                        Snackbar.LENGTH_SHORT
                    ).show()
                    return
                }
            }
        }

        finish()
    }

    fun dateStart(view: View) {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selection = Calendar.getInstance()
                selection.set(Calendar.YEAR, year)
                selection.set(Calendar.MONTH, month)
                selection.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val date = selection.time
                val dateFormat = SimpleDateFormat.getDateInstance()
                binding.dateStart.setText(
                    dateFormat.format(date)
                )
                startDate = date
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun dateEnd(view: View) {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selection = Calendar.getInstance()
                selection.set(Calendar.YEAR, year)
                selection.set(Calendar.MONTH, month)
                selection.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val date = selection.time
                val dateFormat = SimpleDateFormat.getDateInstance()
                binding.dateEnd.setText(
                    dateFormat.format(date)
                )
                endDate = date
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun sourceCash() {
        currentSource = 0

        // TODO: Maybe add animation when selecting source
        binding.photoLeading.setImageResource(R.drawable.ic_dollar)
    }

    private fun sourceSavings() {
        currentSource = 1

        binding.photoLeading.setImageResource(R.drawable.ic_credit_card)

        binding.interestLayout.visibility = View.VISIBLE
        binding.capitalizationLayout.visibility = View.VISIBLE
    }

    private fun sourceInvestment() {
        currentSource = 2

        binding.photoLeading.setImageResource(R.drawable.ic_percent)

        binding.interestLayout.visibility = View.VISIBLE
        binding.capitalizationLayout.visibility = View.VISIBLE
        binding.dateStartLayout.visibility = View.VISIBLE
        binding.dateEndLayout.visibility = View.VISIBLE
    }

    private fun getCapitalization(): Capitalization? {
        val selectedString = binding.capitalization.text.toString()

        Capitalization.values().forEach {
            if (selectedString == resources.getString(it.value)) {
                return it
            }
        }

        return null
    }
}
