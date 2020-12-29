package com.github.kwasow.archipelago.utils

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.github.kwasow.archipelago.data.SourceAccount
import com.github.kwasow.archipelago.data.SourceCash
import com.github.kwasow.archipelago.data.SourceInvestment
import com.github.kwasow.archipelago.data.Transaction
import com.github.kwasow.archipelago.databinding.ViewSourceCardBinding
import java.util.*

class SourceAdapter(private val dataSet: Array<*>)
    : RecyclerView.Adapter<SourceAdapter.ViewHolder>() {

    class ViewHolder(binding: ViewSourceCardBinding) : RecyclerView.ViewHolder(binding.root) {
        // Setup all children views
        val root = binding.root
        val cardView = binding.cardView
        val sourceName = binding.sourceName
        val amount = binding.amount
        val monthChange = binding.monthChange
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewSourceCardBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
        )

        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (val sourceObject = dataSet[position]) {
            is SourceCash -> {
                holder.sourceName.text = sourceObject.name
                holder.amount.text = "" +
                        """${String.format("%.2f", sourceObject.amount)} """ +
                        sourceObject.currency

                val change = getMonthChange(sourceObject.transactions)
                val plus = if (change >= 0) {
                    holder.monthChange.setTextColor(MaterialColors.LIGHT_GREEN)
                    "+"
                } else {
                    holder.monthChange.setTextColor(MaterialColors.RED)
                    ""
                }

                holder.monthChange.text = "($plus" +
                        """${String.format("%.2f", change)} """ +
                        "${sourceObject.currency})"
            }
            is SourceAccount -> {
                holder.sourceName.text = sourceObject.name
                holder.amount.text = "" +
                        """${String.format("%.2f", sourceObject.amount)} """ +
                        sourceObject.currency

                val change = getMonthChange(sourceObject.transactions)
                val plus = if (change >= 0) {
                    holder.monthChange.setTextColor(MaterialColors.LIGHT_GREEN)
                    "+"
                } else {
                    holder.monthChange.setTextColor(MaterialColors.RED)
                    ""
                }

                holder.monthChange.text = "($plus" +
                        """${String.format("%.2f", change)} """ +
                        "${sourceObject.currency})"
            }
            is SourceInvestment -> {
                holder.sourceName.text = sourceObject.name
                holder.amount.text = "" +
                        """${String.format("%.2f", sourceObject.amount)} """ +
                        sourceObject.currency

                // Don't show monthly change
                holder.monthChange.visibility = View.GONE
            }
        }

        // TODO: The click animation is a bit weird
        holder.cardView.setOnClickListener {
            Toast.makeText(holder.root.context, "Not implemented", Toast.LENGTH_SHORT)
                    .show()
        }
    }

    override fun getItemCount(): Int = dataSet.size

    private fun getMonthChange(transactions: Array<Transaction>) : Double {
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        // Set calendar to beginning of this month
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        val beginning = calendar.time

        // Calculate change
        var change = 0.0
        transactions.forEach {
            if (it.date.after(beginning)) {
                change += it.amount
            }
        }

        return change
    }
}