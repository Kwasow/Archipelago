package com.github.kwasow.archipelago.data

import android.content.Context
import com.github.kwasow.archipelago.utils.SourceManager
import java.io.Serializable
import java.util.*

data class SourceInvestment(
        var name: String,
        var country: String,
        var countryCode: String,
        var currency: String,
        var amount: Double,
        var interest: Double,
        val capitalization: SourceManager.Capitalization,
        var start: Date,
        var end: Date
) : Serializable {
    fun save(context: Context): Boolean {
        return SourceManager.save(
                context, name, "/investment", this
        )
    }

    companion object {
        // This is safe - I promise
        @Suppress("UNCHECKED_CAST")
        fun get(context: Context): Array<SourceInvestment> {
            val anyArray = SourceManager.get(
                    context, "/investment"
            )

            // Return empty array if empty
            if (anyArray.isEmpty()) return arrayOf()

            val returnArray = mutableListOf<SourceInvestment>()
            anyArray.forEach {
                if (it is SourceInvestment) {
                    returnArray.add(it)
                }
            }

            return returnArray.toTypedArray()
        }
    }

    override fun toString(): String {
        return "SourceInvestment{$name}"
    }
}