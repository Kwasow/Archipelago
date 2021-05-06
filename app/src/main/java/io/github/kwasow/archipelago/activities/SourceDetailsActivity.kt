package io.github.kwasow.archipelago.activities

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import io.github.kwasow.archipelago.R
import io.github.kwasow.archipelago.adapters.TransactionsAdapter
import io.github.kwasow.archipelago.data.Source
import io.github.kwasow.archipelago.data.SourceAccount
import io.github.kwasow.archipelago.data.Transaction
import io.github.kwasow.archipelago.databinding.ActivitySourceDetailsBinding
import io.github.kwasow.archipelago.utils.MaterialColors
import io.github.kwasow.archipelago.utils.NoScrollLinearLayoutManager
import org.javamoney.moneta.Money

class SourceDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySourceDetailsBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sourceObject = intent.getSerializableExtra(Source.INTENT_PUT_NAME) as SourceAccount
        val transactions: List<Transaction>

        binding = ActivitySourceDetailsBinding.inflate(layoutInflater)
        transactions = sourceObject.transactions

        // Name and amount
        binding.sourceName.text = sourceObject.name
        binding.amount.text =
            Money.of(sourceObject.sum.number, sourceObject.currencyCode).toString()

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

        setupGraph(binding.graph, transactions, change.isPositiveOrZero)

        // Recycler
        val layoutManager = NoScrollLinearLayoutManager(this)
        val adapter = TransactionsAdapter(transactions, sourceObject.currencyCode)
        binding.transactionsRecycler.layoutManager = layoutManager
        binding.transactionsRecycler.adapter = adapter

        setContentView(binding.root)
    }

    private fun setupGraph(
        graph: LineChart,
        transactions: List<Transaction>,
        positiveChange: Boolean
    ) {
        graph.setBackgroundColor(Color.TRANSPARENT)
        graph.description.isEnabled = false
        graph.setTouchEnabled(false)
        graph.setTouchEnabled(false)
        graph.xAxis.position = XAxis.XAxisPosition.BOTTOM
        graph.xAxis.setDrawLabels(false)
        graph.axisRight.setDrawAxisLine(false)
        graph.axisRight.setDrawLabels(false)
        graph.legend.isEnabled = false
        graph.data = setupLineData(transactions, positiveChange)
    }

    private fun setupLineData(transactions: List<Transaction>, positiveChange: Boolean): LineData? {
        if (transactions.size < 2) return null

        val sortedTransactions = sortTransactionsByDate(transactions)
        val chartValues = prepareChartData(sortedTransactions)

        return createLineData(chartValues, positiveChange)
    }

    private fun sortTransactionsByDate(transactions: List<Transaction>): List<Transaction> {
        val returnTransactions = transactions.toTypedArray()
        returnTransactions.sortBy {
            it.date.time
        }
        return returnTransactions.toList()
    }

    private fun prepareChartData(transactions: List<Transaction>): List<Entry> {
        val chartValues = mutableListOf<Entry>()

        for (i in transactions.indices) {
            // Space data evenly
            val date = (i + 1).toFloat()
            val amount = if (i == 0) {
                transactions[0].amount.number.toFloat()
            } else {
                chartValues[i - 1].y + transactions[i].amount.number.toFloat()
            }

            chartValues.add(
                Entry(date, amount)
            )
        }

        return chartValues
    }

    private fun createLineData(chartValues: List<Entry>, positiveChange: Boolean): LineData {
        val set = LineDataSet(chartValues, "")
        set.setDrawFilled(true)
        set.setDrawCircles(false)
        set.setDrawValues(false)
        set.color = MaterialColors.BLACK
        set.fillDrawable = getBackgroundDrawable(positiveChange)

        return LineData(set)
    }

    private fun getBackgroundDrawable(positiveChange: Boolean): Drawable? {
        return if (positiveChange) {
            ContextCompat.getDrawable(this, R.drawable.fade_green)
        } else {
            ContextCompat.getDrawable(this, R.drawable.fade_red)
        }
    }
}
