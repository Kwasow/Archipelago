package com.github.kwasow.archipelago.data

import android.content.Context
import com.github.kwasow.archipelago.utils.SourceManager
import java.io.Serializable

data class SourceCash(
        var name: String,
        var country: String,
        var countryCode: String,
        var currency: String,
        var amount: Double,
        var transactions: MutableList<Transaction>
) : Serializable {
    fun save(context: Context): Boolean {
        return SourceManager.save(
                context, name, "/cash", this
        )
    }

    fun recalculate() {
        amount = SourceManager.recalculate(transactions)
    }

    fun delete(context: Context): Boolean {
        return SourceManager.delete(
                context, name, "/cash"
        )
    }

    fun update(context: Context): Boolean {
        return SourceManager.update(
                context, name, "/cash", this
        )
    }

    companion object {
        // This is safe - I promise
        @Suppress("UNCHECKED_CAST")
        fun get(context: Context): List<SourceCash> {
            val anyList = SourceManager.get(
                    context, "/cash"
            )

            // Return empty list if empty
            if (anyList.isEmpty()) return listOf()

            val returnList = mutableListOf<SourceCash>()
            anyList.forEach {
                if (it is SourceCash) {
                    returnList.add(it)
                }
            }

            return returnList
        }
    }

    override fun toString(): String {
        return "SourceCash{$name}"
    }
}