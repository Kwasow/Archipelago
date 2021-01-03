package com.github.kwasow.archipelago.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.kwasow.archipelago.R
import com.github.kwasow.archipelago.animations.FabAnimation
import com.github.kwasow.archipelago.data.SourceAccount
import com.github.kwasow.archipelago.data.SourceCash
import com.github.kwasow.archipelago.data.SourceInvestment
import com.github.kwasow.archipelago.databinding.ActivityMainBinding
import com.github.kwasow.archipelago.utils.SourceAdapter
import com.github.kwasow.archipelago.views.AddTransactionDialog

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val fabList = mutableListOf<View>()
    var fabIsRotated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        initFab()
        setupOptionsMenu()

        setupRecyclers()

        setContentView(binding.root)
    }

    override fun onBackPressed() {
        if (fabIsRotated) {
            fabClick(binding.actionButton)
        } else {
            super.onBackPressed()
        }
    }

    private fun initFab() {
        // Add all FAB items to a list for easier animations
        fabList.add(binding.actionButtonSource)
        fabList.add(binding.fabCardSource)
        fabList.add(binding.actionButtonTransaction)
        fabList.add(binding.fabCardTransaction)
        fabList.add(binding.actionButtonStock)
        fabList.add(binding.fabCardStock)

        // Get the items off the screen
        fabList.forEach {
            FabAnimation.init(it)
        }

        // Close the FAB menu if user clicks background content
        binding.opacity.setOnClickListener {
            fabClick(binding.actionButton)
        }

        // Prepare for animating
        binding.opacity.alpha = 0F
    }

    fun fabClick(view: View) {
        // Rotate the main FAB icon
        fabIsRotated = FabAnimation.rotate(view, !fabIsRotated)

        // Take care of showing the mini FABs
        if (fabIsRotated) {
            fabList.forEach {
                FabAnimation.showIn(it)
            }

            // Prevent scrolling on main content
            binding.opacity.visibility = View.VISIBLE
            // Animate opacity in
            binding.opacity.animate()
                    .setDuration(250)
                    .alpha(0.25F)
        } else {
            fabList.forEach {
                FabAnimation.showOut(it)
            }

            // Animate opacity out
            binding.opacity.animate()
                    .setDuration(250)
                    .alpha(0F)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)

                            // Get rid of the view when animation is done and restore scrolling
                            if (!fabIsRotated) {
                                binding.opacity.visibility = View.GONE
                            }
                        }
                    })
        }
    }

    private fun setupOptionsMenu() {
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menuSettings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                }
            }

            return@setOnMenuItemClickListener true;
        }
    }

    private fun setupRecyclers() {
        // Set up cash
        val cashList = SourceCash.get(this)
        val cashLayoutManager = LinearLayoutManager(this)
        val cashAdapter = SourceAdapter(cashList)
        binding.cashRecycler.layoutManager = cashLayoutManager
        binding.cashRecycler.adapter = cashAdapter
        if (cashList.isEmpty()) {
            binding.cashEmpty.visibility = View.VISIBLE
        }

        // Set up account
        val accountList = SourceAccount.get(this)
        val accountLayoutManager = LinearLayoutManager(this)
        val accountAdapter = SourceAdapter(accountList)
        binding.accountRecycler.layoutManager = accountLayoutManager
        binding.accountRecycler.adapter = accountAdapter
        if (accountList.isEmpty()) {
            binding.accountEmpty.visibility = View.VISIBLE
        }

        // Set up investment
        val investmentList = SourceInvestment.get(this)
        val investmentLayoutManager = LinearLayoutManager(this)
        val investmentAdapter = SourceAdapter(investmentList)
        binding.investmentRecycler.layoutManager = investmentLayoutManager
        binding.investmentRecycler.adapter = investmentAdapter
        if (investmentList.isEmpty()) {
            binding.investmentEmpty.visibility = View.VISIBLE
        }

    }

    fun addSource(view: View) {
        val intent = Intent(this, AddSourceActivity::class.java)

        // Close fab if it's open (it probably is, just double checking)
        if (fabIsRotated) {
            fabClick(binding.actionButton)
        }

        startActivity(intent)
    }

    fun addTransaction(view: View) {
        fabClick(binding.actionButton)
        AddTransactionDialog(this).show()
    }

    fun buySellStocks(view: View) {
        // TODO: Buy and sell stocks
        Toast.makeText(applicationContext, "Not implemented", Toast.LENGTH_SHORT)
                .show()
    }
}