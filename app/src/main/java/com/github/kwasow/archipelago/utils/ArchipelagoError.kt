package com.github.kwasow.archipelago.utils

import android.util.Log
import java.lang.Exception

class ArchipelagoError {
    companion object {
        private const val tag = "Archipelago"

        fun d(error: String) {
            Log.d(tag, error)
        }

        fun e(error: String) {
            Log.e(tag, error)
        }

        fun e(error: Exception) {
            Log.e(tag, error.toString())
        }
    }
}