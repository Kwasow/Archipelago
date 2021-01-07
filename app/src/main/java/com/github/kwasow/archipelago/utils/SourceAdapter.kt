package com.github.kwasow.archipelago.utils

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.kwasow.archipelago.activities.SourceDetailsActivity
import com.github.kwasow.archipelago.data.Source
import com.github.kwasow.archipelago.data.Source.Companion.getMonthChange
import com.github.kwasow.archipelago.data.SourceAccount
import com.github.kwasow.archipelago.data.SourceCash
import com.github.kwasow.archipelago.data.SourceInvestment
import com.github.kwasow.archipelago.databinding.ViewSourceCardBinding
import com.github.kwasow.archipelago.views.GraphView

class SourceAdapter(private val dataSet: List<Source>) :
    RecyclerView.Adapter<SourceAdapter.ViewHolder>() {

    class ViewHolder(binding: ViewSourceCardBinding) : RecyclerView.ViewHolder(binding.root) {
        // Set up all children views
        val root = binding.root
        val cardView = binding.cardView
        val graph = binding.graph
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
        val sourceObject = dataSet[position]
        when (sourceObject) {
            is SourceCash, is SourceAccount -> {
                // Set name and amount
                holder.sourceName.text = sourceObject.name
                holder.amount.text = "" +
                    """${String.format("%.2f", sourceObject.amount)} """ +
                    sourceObject.currency

                // Set month change
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

                // Set up graph
                holder.graph.data =
                    GraphView.graphArrayFromTransactions(sourceObject.transactions)
            }
            is SourceInvestment -> {
                // Set name and amount
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
            val intent = Intent(holder.root.context, SourceDetailsActivity::class.java)
            intent.putExtra(Source.INTENT_PUT_NAME, sourceObject)
            holder.root.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = dataSet.size
}
