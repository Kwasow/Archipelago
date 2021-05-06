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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.javamoney.moneta.Money

class HomeFragment : Fragment(), PopupMenu.OnMenuItemClickListener {
    private lateinit var binding: FragmentHomeBinding
    private var noSources = false

    private var dataChanged = true

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

        setupRecyclers()
    }

    private fun setupRecyclers() {
        // TODO: Currency is hardcoded
        context?.let { context ->
            GlobalScope.launch {
                val accountList = SourceAccount.get(context)
                var sumAccount = Money.of(0.0, "PLN")
                accountList.forEach { source ->
                    sumAccount = sumAccount.add(source.sum)
                }

                val investmentList = SourceInvestment.get(context)
                var sumInvestment = Money.of(0.0, "PLN")
                investmentList.forEach { source ->
                    sumInvestment = sumInvestment.add(source.sum)
                }

                activity?.runOnUiThread {
                    if (dataChanged) {
                        val accountLayoutManager = NoScrollLinearLayoutManager(context)
                        val accountAdapter = SourceAdapter(accountList)
                        binding.accountRecycler.layoutManager = accountLayoutManager
                        binding.accountRecycler.adapter = accountAdapter

                        val investmentLayoutManager = NoScrollLinearLayoutManager(context)
                        val investmentAdapter = SourceAdapter(investmentList)
                        binding.investmentRecycler.layoutManager = investmentLayoutManager
                        binding.investmentRecycler.adapter = investmentAdapter

                        setupGraph(binding.circularGraph, sumAccount, sumInvestment)
                    }

                    noSources = if (accountList.isEmpty() && investmentList.isEmpty()) {
                        binding.sourcesEmpty.visibility = View.VISIBLE
                        true
                    } else {
                        binding.sourcesEmpty.visibility = View.GONE
                        false
                    }
                    dataChanged = false
                }
            }
        }
    }

    private fun setupGraph(graph: PieChart, sumAccount: Money, sumInvestment: Money) {
        graph.setUsePercentValues(true)
        graph.centerText = sumInvestment.add(sumAccount).toString()
        graph.isRotationEnabled = false
        graph.legend.isEnabled = false
        graph.description.isEnabled = false
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
                dataChanged = true
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
                    dataChanged = true
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
