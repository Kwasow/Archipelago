package io.github.kwasow.archipelago.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.kwasow.archipelago.data.Transaction
import io.github.kwasow.archipelago.databinding.ViewTransactionItemBinding
import io.github.kwasow.archipelago.utils.MaterialColors
import org.javamoney.moneta.Money

class TransactionsAdapter(
    private val dataSet: List<Transaction>,
    private val currency: String
) : RecyclerView.Adapter<TransactionsAdapter.ViewHolder>() {

    class ViewHolder(binding: ViewTransactionItemBinding) : RecyclerView.ViewHolder(binding.root) {
        // Set up all children views
        val root = binding.root
        val transactionName = binding.transactionName
        val transactionValue = binding.transactionValue
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewTransactionItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = dataSet[dataSet.size - position - 1]
        // Transaction name
        holder.transactionName.text = transaction.name

        // Transaction value
        val plus = if (transaction.amount.isPositiveOrZero) {
            holder.transactionValue.setTextColor(MaterialColors.LIGHT_GREEN)
            "+"
        } else {
            holder.transactionValue.setTextColor(MaterialColors.RED)
            ""
        }
        holder.transactionValue.text = plus +
            Money.of(transaction.amount.number, currency)
    }

    override fun getItemCount(): Int = dataSet.size
}
