package com.github.kwasow.archipelago.views

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.widget.ArrayAdapter
import com.github.kwasow.archipelago.R
import com.github.kwasow.archipelago.data.SourceAccount
import com.github.kwasow.archipelago.data.SourceCash
import com.github.kwasow.archipelago.databinding.DialogAddTransactionBinding
import com.github.kwasow.archipelago.utils.ArchipelagoError

class AddTransactionDialog(context: Context) : AlertDialog(context) {
    private lateinit var binding: DialogAddTransactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DialogAddTransactionBinding.inflate(layoutInflater)
        ArchipelagoError.d("onCreate")

        setupSources()
        setupKeyboard()
        binding.finishButton.setOnClickListener { addTransaction() }

        setContentView(binding.root)
    }

    private fun setupSources() {
        val sources = mutableListOf<String>()

        val cashSources = SourceCash.get(this.context)
        cashSources.forEach {
            sources.add(
                    "${it.name} (${it.amount} ${it.currency})"
            )
        }

        val accountSources = SourceAccount.get(this.context)
        accountSources.forEach {
            sources.add(
                    "${it.name} (${it.amount} ${it.currency})"
            )
        }

        val sourcesAdapter = ArrayAdapter(this.context, R.layout.list_item, sources)
        binding.sourceSelect.setAdapter(sourcesAdapter)
        binding.sourceSelect.setOnItemClickListener { _, _, i, _ ->
            if (i > cashSources.size) {
                // Then it's from the accountSources list
                binding.amount.setCurrency(
                        accountSources[i - cashSources.size].currency
                )
            } else {
                // It's form cash sources
                binding.amount.setCurrency(
                        cashSources[i].currency
                )
            }
        }
    }

    private fun setupKeyboard() {
        window?.clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        binding.amount.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            } else {
                window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

            }
        }
    }

    private fun addTransaction() {
        dismiss()
    }
}