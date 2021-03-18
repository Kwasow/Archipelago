package io.github.kwasow.archipelago.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.kwasow.archipelago.activities.SourceDetailsActivity
import io.github.kwasow.archipelago.data.Source
import io.github.kwasow.archipelago.data.Source.Companion.getMonthChange
import io.github.kwasow.archipelago.data.SourceAccount
import io.github.kwasow.archipelago.data.SourceInvestment
import io.github.kwasow.archipelago.databinding.ViewSourceCardBinding
import io.github.kwasow.archipelago.utils.MaterialColors
import io.github.kwasow.archipelago.views.GraphView
import org.javamoney.moneta.Money

class SourceAdapter(private val dataSet: List<Source>) :
    RecyclerView.Adapter<SourceAdapter.ViewHolder>() {

    class ViewHolder(binding: ViewSourceCardBinding) : RecyclerView.ViewHolder(binding.root) {
        // Set up all children views
        val root = binding.root
        val cardView = binding.cardView
        val graph = binding.graph
        val sourceName = binding.sourceName
        val amount = binding.amount
        val monthChangeLabel = binding.monthChangeLabel
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
            is SourceAccount -> {
                // Set name and amount
                holder.sourceName.text = sourceObject.name
                holder.amount.text =
                    Money.of(sourceObject.amount.number, sourceObject.currencyCode).toString()

                // Set month change
                val change = getMonthChange(sourceObject.transactions, sourceObject.currencyCode)
                val plus = if (change.isPositiveOrZero) {
                    holder.monthChange.setTextColor(MaterialColors.LIGHT_GREEN)
                    "+"
                } else {
                    holder.monthChange.setTextColor(MaterialColors.RED)
                    ""
                }

                holder.monthChange.text = plus +
                    Money.of(change.number, sourceObject.currencyCode)

                // Set up graph
                holder.graph.data =
                    GraphView.graphArrayFromTransactions(sourceObject.transactions)
            }
            is SourceInvestment -> {
                // Set name and amount
                holder.sourceName.text = sourceObject.name
                holder.amount.text =
                    Money.of(sourceObject.amount.number, sourceObject.currencyCode).toString()

                // Don't show monthly change
                holder.monthChangeLabel.visibility = View.GONE
                holder.monthChange.visibility = View.GONE
                holder.graph.visibility = View.GONE
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
