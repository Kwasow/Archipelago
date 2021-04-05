package io.github.kwasow.archipelago.views

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import com.google.android.material.snackbar.Snackbar
import io.github.kwasow.archipelago.R
import io.github.kwasow.archipelago.data.Capitalization
import io.github.kwasow.archipelago.data.SourceAccount
import io.github.kwasow.archipelago.data.SourceInvestment
import io.github.kwasow.archipelago.data.Transaction
import io.github.kwasow.archipelago.databinding.DialogAddSourceBinding
import io.github.kwasow.archipelago.utils.CountryManager
import org.javamoney.moneta.Money
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class AddSourceDialog(context: Context) : AlertDialog(context) {
    private lateinit var binding: DialogAddSourceBinding
    private var currentSource = 0
    private var countryChosen = false
    private var capChosen = false

    private var startDate: Date? = null
    private var endDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DialogAddSourceBinding.inflate(layoutInflater)

        setupKeyboard()
        setupButtons()
        setupSourcePicker()
        setupCountryPicker()
        setupCapitalizationOptions()

        setContentView(binding.root)
    }

    private fun setupSourcePicker() {
        val sources = getSourceTypesList()
        val sourcesAdapter = ArrayAdapter(context, R.layout.list_item, sources)
        binding.sourceType.setAdapter(sourcesAdapter)

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
                0 -> sourceSavings()
                1 -> sourceInvestment()
            }
        }
    }

    private fun getSourceTypesList(): List<String> {
        return listOf(
            context.resources.getString(R.string.new_savings_account),
            context.resources.getString(R.string.new_investment)
        )
    }

    private fun setupCountryPicker() {
        val countries = CountryManager.getCountries(context)
        val countryCodes = mutableListOf<String>()
        countries.forEach {
            countryCodes.add(it.code)
        }
        val countryAdapter = ArrayAdapter(context, R.layout.list_item, countryCodes)
        binding.countrySelect.setAdapter(countryAdapter)
        // Set currency when country is selected
        binding.countrySelect.setOnItemClickListener { _, _, i, _ ->
            binding.amount.currencyCode = countries[i].currencyCode
            countryChosen = true
        }
    }

    private fun setupCapitalizationOptions() {
        val caps = listOf(
            context.resources.getString(Capitalization.EndOfMonth.value),
            context.resources.getString(Capitalization.EndOfInvestment.value),
            context.resources.getString(Capitalization.Monthly.value),
            context.resources.getString(Capitalization.Yearly.value)
        )
        val capAdapter = ArrayAdapter(context, R.layout.list_item, caps)
        binding.capitalization.setAdapter(capAdapter)
        binding.capitalization.setOnItemClickListener { _, _, _, _ -> capChosen = true }

        binding.interest.currency = "%"
    }

    private fun createInitialTransactionList(amount: Money): MutableList<Transaction> {
        return mutableListOf(
            Transaction(
                Date(),
                context.resources.getString(R.string.initial_account_state),
                amount,
                context.resources.getString(R.string.added_automatically)
            )
        )
    }

    private fun sourceSavings() {
        currentSource = 0

        binding.photoLeading.setImageResource(R.drawable.ic_credit_card)

        binding.interestLayout.visibility = View.VISIBLE
        binding.capitalizationLayout.visibility = View.VISIBLE
    }

    private fun sourceInvestment() {
        currentSource = 1

        binding.photoLeading.setImageResource(R.drawable.ic_percent)

        binding.interestLayout.visibility = View.VISIBLE
        binding.capitalizationLayout.visibility = View.VISIBLE
        binding.dateStartLayout.visibility = View.VISIBLE
        binding.dateEndLayout.visibility = View.VISIBLE
    }

    private fun getCapitalization(): Capitalization? {
        val selectedString = binding.capitalization.text.toString()

        Capitalization.values().forEach {
            if (selectedString == context.resources.getString(it.value)) {
                return it
            }
        }

        return null
    }

    private fun setupKeyboard() {
        window?.clearFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
        )
        val focusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            } else {
                window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            }
        }

        binding.sourceName.onFocusChangeListener = focusChangeListener
        binding.amount.onFocusChangeListener = focusChangeListener
        binding.interest.onFocusChangeListener = focusChangeListener
    }

    private fun setupButtons() {
        binding.cancelButton.setOnClickListener {
            onBackPressed()
        }
        binding.finishButton.setOnClickListener {
            finishAdding()
        }
        binding.dateStart.setOnClickListener {
            dateStart()
        }
        binding.dateEnd.setOnClickListener {
            dateEnd()
        }
    }

    private fun finishAdding() {
        if (!checkIfAllFieldsCorrect()) return

        // Get common source attributes
        val name = binding.sourceName.text.toString()
        val countryTmp = CountryManager
            .getCountyByCode(context, binding.countrySelect.text.toString())
        val country = countryTmp.name
        val countryCode = countryTmp.code
        val currency = countryTmp.currencyCode
        val amount = binding.amount.getMoneyValue()

        val transactions = createInitialTransactionList(amount)

        // Specific stuff (+checking if succeeded)
        when (currentSource) {
            // Savings account
            0 -> {
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
                if (!source.save(context)) {
                    Snackbar.make(
                        binding.root,
                        R.string.something_went_wrong,
                        Snackbar.LENGTH_SHORT
                    ).show()
                    return
                }
            }
            // Investment
            1 -> {
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
                if (!source.save(context)) {
                    Snackbar.make(
                        binding.root,
                        R.string.something_went_wrong,
                        Snackbar.LENGTH_SHORT
                    ).show()
                    return
                }
            }
        }

        dismiss()
    }

    private fun checkIfAllFieldsCorrect(): Boolean {
        var correct = true
        if (binding.sourceName.text.toString().isBlank()) {
            binding.sourceNameLayout.error = context.resources.getString(R.string.error_blank)
            correct = false
        } else {
            binding.sourceNameLayout.error = null
        }

        if (!countryChosen) {
            binding.countrySelectLayout.error = context.resources.getString(R.string.error_blank)
            correct = false
        } else {
            binding.countrySelectLayout.error = null
        }

        if (!capChosen) {
            binding.capitalizationLayout.error = context.resources.getString(R.string.error_blank)
            correct = false
        } else {
            binding.capitalizationLayout.error = null
        }

        if (currentSource == 1) {
            if (binding.dateStart.text.toString().isBlank()) {
                binding.dateStartLayout.error = context.resources.getString(R.string.error_blank)
                correct = false
            } else {
                binding.dateStartLayout.error = null
            }

            if (binding.dateEnd.text.toString().isBlank()) {
                binding.dateEndLayout.error = context.resources.getString(R.string.error_blank)
                correct = false
            } else {
                binding.dateEndLayout.error = null
            }

            if (endDate != null && startDate != null) {
                if (endDate!!.before(startDate)) {
                    binding.dateEndLayout.error = context.resources.getString(R.string.error_endDate_before_startDate)
                    correct = false
                } else if (startDate!!.before(endDate)) {
                    binding.dateEndLayout.error = null
                }
            }
        }

        return correct
    }

    private fun dateStart() {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            context,
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

    private fun dateEnd() {
        val calendar = Calendar.getInstance()

        DatePickerDialog(
            context,
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
}
