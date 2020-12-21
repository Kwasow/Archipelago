package com.github.kwasow.archipelago.activities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.github.kwasow.archipelago.R
import com.github.kwasow.archipelago.animations.FabAnimation
import com.github.kwasow.archipelago.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val fabList = mutableListOf<View>()
    var fabIsRotated = false
    private lateinit var colorDrawables: Array<ColorDrawable>
    private lateinit var transitionDrawable: TransitionDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        initFab()

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
        binding.coordinator.setOnClickListener {
            fabClick(binding.actionButton)
        }

        // Prepare for animating
        colorDrawables = arrayOf(
                ColorDrawable(Color.TRANSPARENT),
                ColorDrawable(resources.getColor(R.color.black40p))
        )
        transitionDrawable = TransitionDrawable(colorDrawables)
        binding.coordinator.foreground = transitionDrawable
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
            binding.homeScroll.scrolling = false;
            // Animate opacity in
            transitionDrawable.startTransition(250)
        } else {
            fabList.forEach {
                FabAnimation.showOut(it)
            }

            // Enable scrolling back
            binding.homeScroll.scrolling = true;
            // Animate opacity out
            transitionDrawable.reverseTransition(250)
        }


    }
}