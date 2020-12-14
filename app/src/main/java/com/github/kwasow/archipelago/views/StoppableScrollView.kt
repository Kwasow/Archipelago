package com.github.kwasow.archipelago.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.amar.library.ui.StickyScrollView

// This is a scroll view, who's scrolling can be blocked
class StoppableScrollView : StickyScrollView {
    var scrolling = true

    constructor
            (context: Context) : super(context)
    constructor
            (context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor
            (context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        // Check if scrolling is enabled before taking any action
        return if (scrolling) {
            super.onInterceptTouchEvent(ev)
        } else {
            false
        }
    }
}