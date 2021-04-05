package io.github.kwasow.archipelago.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.kwasow.archipelago.adapters.TransactionsAdapter
import io.github.kwasow.archipelago.data.Source
import io.github.kwasow.archipelago.data.SourceAccount
import io.github.kwasow.archipelago.data.Transaction
import io.github.kwasow.archipelago.databinding.ActivitySourceDetailsBinding
import io.github.kwasow.archipelago.utils.MaterialColors
import io.github.kwasow.archipelago.utils.NoScrollLinearLayoutManager
import io.github.kwasow.archipelago.views.GraphView
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
            is SourceAccount -> {
                transactions = sourceObject.transactions

                // Name and amount
                binding.sourceName.text = sourceObject.name
                binding.amount.text =
                    Money.of(sourceObject.amount.number, sourceObject.currencyCode).toString()

                // Month change
                // Set month change
                val change = Source.getMonthChange(sourceObject.transactions, sourceObject.currencyCode)
                val plus = if (change.isPositiveOrZero) {
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
