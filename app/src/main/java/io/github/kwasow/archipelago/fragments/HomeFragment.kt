package io.github.kwasow.archipelago.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.github.kwasow.archipelago.adapters.SourceAdapter
import io.github.kwasow.archipelago.data.SourceAccount
import io.github.kwasow.archipelago.data.SourceInvestment
import io.github.kwasow.archipelago.databinding.FragmentHomeBinding
import io.github.kwasow.archipelago.utils.NoScrollLinearLayoutManager
import org.javamoney.moneta.Money

class HomeFragment : Fragment(), View.OnClickListener {
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

            if (accountList.isEmpty() && investmentList.isEmpty()) {
                binding.sourcesEmpty.visibility = View.VISIBLE
            } else {
                binding.sourcesEmpty.visibility = View.GONE
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.addButton.id -> addButtonClick()
        }
    }

    private fun addButtonClick() {

    }
}
