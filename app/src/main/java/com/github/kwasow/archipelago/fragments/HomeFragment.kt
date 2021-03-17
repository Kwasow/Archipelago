package com.github.kwasow.archipelago.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.kwasow.archipelago.adapters.SourceAdapter
import com.github.kwasow.archipelago.data.SourceAccount
import com.github.kwasow.archipelago.data.SourceCash
import com.github.kwasow.archipelago.data.SourceInvestment
import com.github.kwasow.archipelago.databinding.FragmentHomeBinding
import com.github.kwasow.archipelago.utils.NoScrollLinearLayoutManager
import org.javamoney.moneta.Money

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

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
    }

    override fun onResume() {
        super.onResume()

        // TODO: I think this could be optimized
        setupRecyclers()
    }

    private fun setupRecyclers() {
        context?.let {
            // Set up cash
            val cashList = SourceCash.get(it)
            val cashLayoutManager = NoScrollLinearLayoutManager(it)
            val cashAdapter = SourceAdapter(cashList)
            binding.cashRecycler.layoutManager = cashLayoutManager
            binding.cashRecycler.adapter = cashAdapter
            if (cashList.isEmpty()) {
                binding.cashEmpty.visibility = View.VISIBLE
            } else {
                binding.cashEmpty.visibility = View.GONE
            }
            var sumCash = Money.of(0.0, "PLN")
            cashList.forEach {
                sumCash = sumCash.add(it.amount)
            }

            // Set up account
            val accountList = SourceAccount.get(it)
            val accountLayoutManager = NoScrollLinearLayoutManager(it)
            val accountAdapter = SourceAdapter(accountList)
            binding.accountRecycler.layoutManager = accountLayoutManager
            binding.accountRecycler.adapter = accountAdapter
            if (accountList.isEmpty()) {
                binding.accountEmpty.visibility = View.VISIBLE
            } else {
                binding.accountEmpty.visibility = View.GONE
            }
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
            if (investmentList.isEmpty()) {
                binding.investmentEmpty.visibility = View.VISIBLE
            } else {
                binding.investmentEmpty.visibility = View.GONE
            }
            var sumInvestment = Money.of(0.0, "PLN")
            investmentList.forEach {
                sumInvestment = sumInvestment.add(it.amount)
            }

            // Set up summary graph
            // TODO: This should not be hardcoded
            binding.circularGraph.setData(
                listOf(sumCash, sumAccount, sumInvestment),
                "PLN"
            )
        }
    }
}
