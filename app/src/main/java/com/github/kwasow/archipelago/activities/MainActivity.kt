package com.github.kwasow.archipelago.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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

        setContentView(binding.root)
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

        // Make the opacity layer completely transparent
        binding.opacityLayout.alpha = 0F
        // Close the FAB menu if user clicks background content
        binding.opacityLayout.setOnClickListener {
            fabClick(binding.actionButton)
        }
    }

    fun fabClick(view: View) {
        // Rotate the main FAB icon
        fabIsRotated = FabAnimation.rotate(view, !fabIsRotated)

        // Take care of showing the mini FABs
        if (fabIsRotated) {
            fabList.forEach {
                FabAnimation.showIn(it)
            }

            // Show the opacity layer with an animation
            binding.opacityLayout.visibility = View.VISIBLE
            binding.opacityLayout.animate()
                    .setDuration(250)
                    .alpha(0.25F)
            // Prevent scrolling on main content
            binding.homeScroll.scrolling = false
        } else {
            fabList.forEach {
                FabAnimation.showOut(it)
            }

            // Enable scrolling back
            binding.homeScroll.scrolling = true
            // Animate the opacity layer out
            binding.opacityLayout.animate()
                    .setDuration(250)
                    .alpha(0F)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)

                            // Get rid of the opacity layer after the animation is done
                            if (!fabIsRotated) {
                                binding.opacityLayout.visibility = View.GONE
                            }
                        }
                    })
        }
    }
}