package com.github.kwasow.archipelago.views

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.widget.ArrayAdapter
import com.github.kwasow.archipelago.R
import com.github.kwasow.archipelago.data.SourceAccount
import com.github.kwasow.archipelago.data.SourceCash
import com.github.kwasow.archipelago.data.Transaction
import com.github.kwasow.archipelago.databinding.DialogAddTransactionBinding
import com.google.android.material.snackbar.Snackbar
import java.util.Date

class AddTransactionDialog(context: Context) : AlertDialog(context) {
    private lateinit var binding: DialogAddTransactionBinding
    var onAddListener = {}

    private var currentSelection: Int = -1
    private lateinit var cashSources: List<SourceCash>
    private lateinit var accountSources: List<SourceAccount>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DialogAddTransactionBinding.inflate(layoutInflater)

        setupSources()
        setupKeyboard()
        binding.finishButton.setOnClickListener { addTransaction() }

        setContentView(binding.root)
    }

    private fun setupSources() {
        val sources = mutableListOf<String>()

        cashSources = SourceCash.get(this.context)
        cashSources.forEach {
            sources.add(
                "${it.name} (${it.amount} ${it.currencyCode})"
            )
        }

        accountSources = SourceAccount.get(this.context)
        accountSources.forEach {
            sources.add(
                "${it.name} (${it.amount} ${it.currencyCode})"
            )
        }

        val sourcesAdapter = ArrayAdapter(this.context, R.layout.list_item, sources)
        binding.sourceSelect.setAdapter(sourcesAdapter)
        binding.sourceSelect.setOnItemClickListener { _, _, i, _ ->
            currentSelection = i

            if (i >= cashSources.size) {
                // Then it's from the accountSources list
                binding.amount.currency = accountSources[i - cashSources.size].currencyCode
            } else {
                // It's form cash sources
                binding.amount.currency = cashSources[i].currencyCode
            }
        }
    }

    private fun setupKeyboard() {
        window?.clearFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
        )
        binding.amount.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            } else {
                window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            }
        }
    }

    private fun addTransaction() {
        var error = false

        // Check if name given
        if (binding.transactionName.text.toString().isBlank()) {
            binding.transactionNameLayout.error = context.getString(R.string.error_blank)
            error = true
        } else {
            binding.transactionNameLayout.error = null
        }

        // Check if source selected
        if (currentSelection == -1) {
            binding.sourceSelectLayout.error = context.getString(R.string.error_blank)
            error = true
        } else {
            binding.sourceSelectLayout.error = null
        }

        // If neither is selected
        // TODO: Error is different color than rest of material components
        if (binding.radioGroup.checkedRadioButtonId == -1) {
            binding.radioButtonTakeOut.error = context.getString(R.string.choose_option)
            error = true
        } else {
            binding.radioButtonTakeOut.error = null
        }

        // Return if errors
        if (error) return

        // Get the details
        val amount = if (binding.radioGroup.checkedRadioButtonId == R.id.radioButtonTakeOut) {
            binding.amount.getMoneyValue().negate()
        } else {
            binding.amount.getMoneyValue()
        }

        val transaction = Transaction(
            Date(),
            binding.transactionName.text.toString(),
            amount,
            binding.details.text.toString()
        )

        if (currentSelection > cashSources.size) {
            // Then it's from the accountSources list
            val source = accountSources[currentSelection - cashSources.size]
            source.transactions.add(transaction)
            source.recalculate()
            // Check if transaction added successfully
            if (!source.update(context)) {
                Snackbar.make(
                    binding.root,
                    R.string.something_went_wrong,
                    Snackbar.LENGTH_SHORT
                ).show()
                return
            }
        } else {
            // It's form cash sources
            val source = cashSources[currentSelection]
            source.transactions.add(transaction)
            source.recalculate()
            // Check if transaction added successfully
            if (!source.update(context)) {
                Snackbar.make(
                    binding.root,
                    R.string.something_went_wrong,
                    Snackbar.LENGTH_SHORT
                ).show()
                return
            }
        }

        dismiss()
        onAddListener()
    }
}
