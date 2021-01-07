package com.github.kwasow.archipelago.data

import android.content.Context
import com.github.kwasow.archipelago.utils.SourceManager
import java.util.Date

data class SourceInvestment(
    override var name: String,
    override var country: String,
    override var countryCode: String,
    override var currency: String,
    override var amount: Double,
    var interest: Double,
    val capitalization: SourceManager.Capitalization,
    var start: Date,
    var end: Date
) : Source {
    override var transactions: MutableList<Transaction> = mutableListOf()

    fun save(context: Context): Boolean {
        return SourceManager.save(
            context, name, "/investment", this
        )
    }

    fun delete(context: Context): Boolean {
        return SourceManager.delete(
            context, name, "/investment"
        )
    }

    fun update(context: Context): Boolean {
        return SourceManager.update(
            context, name, "/investment", this
        )
    }

    companion object {
        // This is safe - I promise
        @Suppress("UNCHECKED_CAST")
        fun get(context: Context): List<SourceInvestment> {
            val anyList = SourceManager.get(
                context, "/investment"
            )

            // Return empty list if empty
            if (anyList.isEmpty()) return listOf()

            val returnList = mutableListOf<SourceInvestment>()
            anyList.forEach {
                if (it is SourceInvestment) {
                    returnList.add(it)
                }
            }

            return returnList
        }
    }

    override fun toString(): String {
        return "SourceInvestment{$name}"
    }
}
