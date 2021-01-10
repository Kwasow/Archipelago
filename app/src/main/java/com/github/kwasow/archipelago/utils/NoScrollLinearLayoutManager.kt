package com.github.kwasow.archipelago.utils

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

class NoScrollLinearLayoutManager(context: Context) : LinearLayoutManager(context) {

    override fun canScrollHorizontally(): Boolean = false

    override fun canScrollVertically(): Boolean = false
}