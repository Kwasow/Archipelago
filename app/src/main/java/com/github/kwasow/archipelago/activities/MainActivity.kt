package com.github.kwasow.archipelago.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.kwasow.archipelago.R
import com.github.kwasow.archipelago.animations.FabAnimation
import com.github.kwasow.archipelago.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val fabList = mutableListOf<View>()
    var fabIsRotated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        initFab()
        setupOptionsMenu()

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
        fabList.add(binding.actionButtonCash)
        fabList.add(binding.actionButtonSavings)
        fabList.add(binding.actionButtonInvestment)
        fabList.add(binding.actionButtonStock)
        fabList.add(binding.fabCardCash)
        fabList.add(binding.fabCardSavings)
        fabList.add(binding.fabCardInvestment)
        fabList.add(binding.fabCardStocks)

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

    fun addSource(view: View) {
        val intent = Intent(this, AddSourceActivity::class.java)

        /*
        Pass info about the type of funds being added as an intent extra
        0 - something went wrong
        1 - cash
        2 - savings account
        3 - investment
        4 - stocks
        */
        when (view) {
            binding.actionButtonCash -> intent.putExtra("sourceType", 1)
            binding.actionButtonSavings -> intent.putExtra("sourceType", 2)
            binding.actionButtonInvestment -> intent.putExtra("sourceType", 3)
            binding.actionButtonStock -> intent.putExtra("sourceType", 4)
            else -> intent.putExtra("sourceType", 0)
        }

        // Close fab if it's open (it probably is, just double checking)
        if (fabIsRotated) {
            fabClick(binding.actionButton)
        }

        startActivity(intent)
    }
}