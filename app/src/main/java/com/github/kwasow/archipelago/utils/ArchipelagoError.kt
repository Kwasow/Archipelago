package com.github.kwasow.archipelago.utils

import android.util.Log

class ArchipelagoError {
    companion object {
        private const val tag = "Archipelago"

        fun d(error: String) {
            Log.d(tag, error)
        }
    }
}