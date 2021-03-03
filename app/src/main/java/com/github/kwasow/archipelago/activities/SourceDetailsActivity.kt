package com.github.kwasow.archipelago.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.kwasow.archipelago.data.Source
import com.github.kwasow.archipelago.data.SourceAccount
import com.github.kwasow.archipelago.data.SourceCash
import com.github.kwasow.archipelago.data.Transaction
import com.github.kwasow.archipelago.databinding.ActivitySourceDetailsBinding
import com.github.kwasow.archipelago.utils.MaterialColors
import com.github.kwasow.archipelago.utils.NoScrollLinearLayoutManager
import com.github.kwasow.archipelago.utils.TransactionsAdapter
import com.github.kwasow.archipelago.views.GraphView
import org.javamoney.moneta.Money

class SourceDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySourceDetailsBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sourceObject = intent.getSerializableExtra(Source.INTENT_PUT_NAME)
        val transactions: List<Transaction>

        binding = ActivitySourceDetailsBinding.inflate(layoutInflater)

        when (sourceObject) {
            is SourceCash, is SourceAccount -> {
                transactions = (sourceObject as Source).transactions

                // Name and amount
                binding.sourceName.text = sourceObject.name
                binding.amount.text =
                        Money.of(sourceObject.amount.number, sourceObject.currencyCode).toString()

                // Month change
                // Set month change
                val change = Source.getMonthChange(sourceObject.transactions, sourceObject.currencyCode)
                val plus = if (change.isPositive) {
                    binding.monthChange.setTextColor(MaterialColors.LIGHT_GREEN)
                    "+"
                } else {
                    binding.monthChange.setTextColor(MaterialColors.RED)
                    ""
                }
                binding.monthChange.text = plus +
                    Money.of(change.number, sourceObject.currencyCode)
                // Graph data
                binding.graph.data =
                    GraphView.graphArrayFromTransactions(transactions)

                // Recycler
                val layoutManager = NoScrollLinearLayoutManager(this)
                val adapter = TransactionsAdapter(transactions, sourceObject.currencyCode)
                binding.transactionsRecycler.layoutManager = layoutManager
                binding.transactionsRecycler.adapter = adapter
            }
        }

        setContentView(binding.root)
    }
}
