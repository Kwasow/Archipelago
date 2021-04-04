package io.github.kwasow.archipelago.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import io.github.kwasow.archipelago.R
import io.github.kwasow.archipelago.adapters.SourceAdapter
import io.github.kwasow.archipelago.data.SourceAccount
import io.github.kwasow.archipelago.data.SourceInvestment
import io.github.kwasow.archipelago.databinding.FragmentHomeBinding
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
        context?.let {
            // Set up account
            val accountList = SourceAccount.get(it)
            val accountLayoutManager = NoScrollLinearLayoutManager(it)
            val accountAdapter = SourceAdapter(accountList)
            binding.accountRecycler.layoutManager = accountLayoutManager
            binding.accountRecycler.adapter = accountAdapter
            var sumAccount = Money.of(0.0, "PLN")
            accountList.forEach {
                sumAccount = sumAccount.add(it.amount)
            }

            // Set up investment
            val investmentList = SourceInvestment.get(it)
            val investmentLayoutManager = NoScrollLinearLayoutManager(it)
            val investmentAdapter = SourceAdapter(investmentList)
            binding.investmentRecycler.layoutManager = investmentLayoutManager
            binding.investmentRecycler.adapter = investmentAdapter
            var sumInvestment = Money.of(0.0, "PLN")
            investmentList.forEach {
                sumInvestment = sumInvestment.add(it.amount)
            }

            // Set up summary graph
            // TODO: This should not be hardcoded
            binding.circularGraph.setData(
                listOf(sumAccount, sumInvestment),
                "PLN"
            )

            noSources = if (accountList.isEmpty() && investmentList.isEmpty()) {
                binding.sourcesEmpty.visibility = View.VISIBLE
                true
            } else {
                binding.sourcesEmpty.visibility = View.GONE
                false
            }
        }
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
