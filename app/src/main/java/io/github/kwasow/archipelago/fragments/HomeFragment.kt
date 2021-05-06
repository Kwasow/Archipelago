package io.github.kwasow.archipelago.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.snackbar.Snackbar
import io.github.kwasow.archipelago.R
import io.github.kwasow.archipelago.adapters.SourceAdapter
import io.github.kwasow.archipelago.data.SourceAccount
import io.github.kwasow.archipelago.data.SourceInvestment
import io.github.kwasow.archipelago.databinding.FragmentHomeBinding
import io.github.kwasow.archipelago.utils.MaterialColors
import io.github.kwasow.archipelago.utils.NoScrollLinearLayoutManager
import io.github.kwasow.archipelago.views.AddSourceDialog
import io.github.kwasow.archipelago.views.AddTransactionDialog
import org.javamoney.moneta.Money

class HomeFragment : Fragment(), PopupMenu.OnMenuItemClickListener {
    private lateinit var binding: FragmentHomeBinding
    private var noSources = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        setupRecyclers()
        setupClicks()
    }

    override fun onResume() {
        super.onResume()

        // TODO: I think this could be optimized
        setupRecyclers()
    }

    private fun setupRecyclers() {
        // TODO: Currency is hardcoded
        context?.let {
            // Set up account
            val accountList = SourceAccount.get(it)
            val accountLayoutManager = NoScrollLinearLayoutManager(it)
            val accountAdapter = SourceAdapter(accountList)
            binding.accountRecycler.layoutManager = accountLayoutManager
            binding.accountRecycler.adapter = accountAdapter
            var sumAccount = Money.of(0.0, "PLN")
            accountList.forEach { source ->
                sumAccount = sumAccount.add(source.sum)
            }

            // Set up investment
            val investmentList = SourceInvestment.get(it)
            val investmentLayoutManager = NoScrollLinearLayoutManager(it)
            val investmentAdapter = SourceAdapter(investmentList)
            binding.investmentRecycler.layoutManager = investmentLayoutManager
            binding.investmentRecycler.adapter = investmentAdapter
            var sumInvestment = Money.of(0.0, "PLN")
            investmentList.forEach { source ->
                sumInvestment = sumInvestment.add(source.sum)
            }

            // Set up summary graph
            setupGraph(binding.circularGraph, sumAccount, sumInvestment)

            noSources = if (accountList.isEmpty() && investmentList.isEmpty()) {
                binding.sourcesEmpty.visibility = View.VISIBLE
                true
            } else {
                binding.sourcesEmpty.visibility = View.GONE
                false
            }
        }
    }

    private fun setupGraph(graph: PieChart, sumAccount: Money, sumInvestment: Money) {
        graph.setUsePercentValues(true)
        graph.centerText = sumInvestment.add(sumAccount).toString()
        graph.isRotationEnabled = false
        graph.animateY(500, Easing.EaseInOutQuad)
        setData(sumAccount, sumInvestment)
    }

    private fun setData(accountSum: Money, investmentSum: Money) {
        val entries = mutableListOf<PieEntry>()
        if (accountSum.isPositive) {
            entries.add(
                PieEntry(accountSum.number.toFloat(), resources.getString(R.string.savings_account))
            )
        }
        if (investmentSum.isPositive) {
            entries.add(
                PieEntry(investmentSum.number.toFloat(), resources.getString(R.string.investment))
            )
        }

        val pieDataSet = PieDataSet(entries, accountSum.add(investmentSum).toString())
        pieDataSet.setDrawIcons(false)

        val colors = mutableListOf<Int>()

        for (i in ColorTemplate.PASTEL_COLORS) {
            colors.add(i)
        }

        pieDataSet.colors = colors

        val pieData = PieData(pieDataSet)
        pieData.setValueFormatter(PercentFormatter())
        pieData.setValueTextSize(11f);
        pieData.setValueTextColor(MaterialColors.WHITE);
        binding.circularGraph.data = pieData
    }

    private fun setupClicks() {
        binding.addButton.setOnClickListener { addButtonClick(it) }
    }

    private fun addButtonClick(view: View) {
        context?.let {
            PopupMenu(it, view).apply {
                inflate(R.menu.menu_home_add)
                setOnMenuItemClickListener(this@HomeFragment)
                show()
            }
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        item?.let {
            when (item.itemId) {
                R.id.action_add_source -> addSource()
                R.id.action_add_transaction -> addTransaction()
                R.id.action_add_stock -> buySellStocks()
                R.id.action_add_crypto -> buySellCrypto()
            }
        }

        return true
    }

    private fun addSource() {
        context?.let {
            val dialog = AddSourceDialog(it)
            dialog.onAddListener = {
                onResume()
            }
            dialog.show()
        }
    }

    private fun addTransaction() {
        if (noSources) {
            Snackbar.make(
                binding.root,
                R.string.add_sources_to_add_transactions,
                Snackbar.LENGTH_SHORT
            ).apply {
                setAnchorView(R.id.navigationBar)
                show()
            }
        } else {
            context?.let {
                val dialog = AddTransactionDialog(it)
                dialog.onAddListener = {
                    // Reload home screen if transaction added
                    onResume()
                }
                dialog.show()
            }
        }
    }

    private fun buySellStocks() {
        // TODO: Buy and sell stocks
        Toast.makeText(context, "Not implemented", Toast.LENGTH_SHORT)
            .show()
    }

    private fun buySellCrypto() {
        // TODO: Buy and sell crypto
        Toast.makeText(context, "Not implemented", Toast.LENGTH_SHORT)
            .show()
    }
}
