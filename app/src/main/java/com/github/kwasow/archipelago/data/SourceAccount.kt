package com.github.kwasow.archipelago.data

import android.content.Context
import com.github.kwasow.archipelago.utils.SourceManager
import java.io.Serializable

data class SourceAccount(
        var name: String,
        var country: String,
        var countryCode: String,
        var currency: String,
        var amount: Double,
        var interest: Double,
        val capitalization: SourceManager.Capitalization,
        var transactions: MutableList<Transaction>
) : Serializable {
    fun save(context: Context) : Boolean {
        return SourceManager.save(
                context, name, "/account", this
        )
    }

    fun recalculate() {
        amount = SourceManager.recalculate(transactions)
    }

    fun delete(context: Context) : Boolean {
        return SourceManager.delete(
                context, name, "/account"
        )
    }

    fun update(context: Context) : Boolean {
        return SourceManager.update(
                context, name, "/account", this
        )
    }

    companion object {
        // This is safe - I promise
        @Suppress("UNCHECKED_CAST")
        fun get(context: Context) : List<SourceAccount> {
            val anyList = SourceManager.get(
                    context, "/account"
            )

            // Return empty list if empty
            if (anyList.isEmpty()) return listOf()

            val returnList = mutableListOf<SourceAccount>()
            anyList.forEach {
                if (it is SourceAccount) {
                    returnList.add(it)
                }
            }

            return returnList
        }
    }

    override fun toString() : String {
        return "SourceAccount{$name}"
    }
}