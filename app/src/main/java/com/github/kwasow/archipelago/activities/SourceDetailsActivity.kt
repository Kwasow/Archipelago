package com.github.kwasow.archipelago.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.kwasow.archipelago.data.Source
import com.github.kwasow.archipelago.data.SourceAccount
import com.github.kwasow.archipelago.data.SourceCash
import com.github.kwasow.archipelago.data.Transaction
import com.github.kwasow.archipelago.databinding.ActivitySourceDetailsBinding
import com.github.kwasow.archipelago.utils.MaterialColors
import com.github.kwasow.archipelago.utils.TransactionsAdapter
import com.github.kwasow.archipelago.views.GraphView

class SourceDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySourceDetailsBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val someObject = intent.getSerializableExtra(Source.INTENT_PUT_NAME)
        val transactions: List<Transaction>

        binding = ActivitySourceDetailsBinding.inflate(layoutInflater)

        when (someObject) {
            is SourceCash, is SourceAccount -> {
                transactions = (someObject as Source).transactions

                // Name and amount
                binding.sourceName.text = someObject.name
                binding.amount.text = "" +
                    """${String.format("%.2f", someObject.amount)} """ +
                    someObject.currency

                // Month change
                // Set month change
                val change = Source.getMonthChange(someObject.transactions)
                val plus = if (change >= 0) {
                    binding.monthChange.setTextColor(MaterialColors.LIGHT_GREEN)
                    "+"
                } else {
                    binding.monthChange.setTextColor(MaterialColors.RED)
                    ""
                }
                binding.monthChange.text = "($plus" +
                    """${String.format("%.2f", change)} """ +
                    "${someObject.currency})"

                // Graph data
                binding.graph.data =
                    GraphView.graphArrayFromTransactions(transactions)

                // Recycler
                val layoutManager = LinearLayoutManager(this)
                val adapter = TransactionsAdapter(transactions, someObject.currency)
                binding.transactionsRecycler.layoutManager = layoutManager
                binding.transactionsRecycler.adapter = adapter
            }
        }

        setContentView(binding.root)
    }
}
