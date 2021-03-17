package com.github.kwasow.archipelago.activities

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.github.kwasow.archipelago.R
import com.github.kwasow.archipelago.adapters.MainPagerAdapter
import com.github.kwasow.archipelago.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val fabList = mutableListOf<View>()
    var fabIsRotated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        // initFab()
        setupViewPager()

        setContentView(binding.root)
    }
/*
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
*/

    private fun setupViewPager() {
        val pager = binding.mainPager
        val pagerAdapter = MainPagerAdapter(this)
        pager.adapter = pagerAdapter
        pager.isUserInputEnabled = false

        binding.navigationBar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_home -> pager.currentItem = 0
                R.id.action_banking -> pager.currentItem = 1
                R.id.action_stocks -> pager.currentItem = 2
                R.id.action_crypto -> pager.currentItem = 3
                R.id.action_settings -> pager.currentItem = 4
            }

            return@setOnNavigationItemSelectedListener true
        }
    }
/*
    fun addSource(view: View) {
        val intent = Intent(this, AddSourceActivity::class.java)

        // Close fab if it's open (it probably is, just double checking)
        if (fabIsRotated) {
            fabClick(binding.actionButton)
        }

        startActivity(intent)
    }

    fun addTransaction(view: View) {
        if (noSources) {
            // TODO: Maybe raise fab when snackbar is visible
            Snackbar.make(
                binding.root,
                R.string.add_sources_to_add_transactions,
                Snackbar.LENGTH_SHORT
            ).show()
        } else {
            fabClick(binding.actionButton)
            val dialog = AddTransactionDialog(this)
            dialog.onAddListener = {
                // Reload home screen if transaction added
                onResume()
            }
            dialog.show()
        }
    }

    fun buySellStocks(view: View) {
        // TODO: Buy and sell stocks
        Toast.makeText(applicationContext, "Not implemented", Toast.LENGTH_SHORT)
            .show()
    }
 */
}
