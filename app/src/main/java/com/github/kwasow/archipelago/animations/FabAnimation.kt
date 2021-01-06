package com.github.kwasow.archipelago.animations

import android.view.View

// A class that holds all the animations related to FABs
class FabAnimation {

    companion object {

        // Initialize the mini FABs and their descriptions to not be visible
        fun init(view: View) {
            view.visibility = View.GONE
            view.translationY = view.height.toFloat()
            view.alpha = 0F
        }

        // Rotate the icon on the main FAB
        fun rotate(view: View, rotate: Boolean): Boolean {
            view.animate()
                .setDuration(250)
                .rotation(if (rotate) 135F else 0F)

            return rotate
        }

        // Animate mini FABs and their descriptions onto the screen
        fun showIn(view: View) {
            view.visibility = View.VISIBLE
            view.alpha = 0F
            view.translationY = view.height.toFloat()
            view.animate()
                .setDuration(250)
                .translationY(0F)
                .alpha(1F)
                .start()
        }

        // Animate mini FABs and their descriptions off the screen
        fun showOut(view: View) {
            view.visibility = View.VISIBLE
            view.alpha = 1F
            view.translationY = 0F
            view.animate()
                .setDuration(250)
                .translationY(view.height.toFloat())
                .alpha(0F)
                .start()
        }
    }
}
