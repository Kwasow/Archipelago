package com.github.kwasow.archipelago.data

import android.content.Context
import com.github.kwasow.archipelago.utils.SourceManager

data class SourceAccount(
    override var name: String,
    override var country: String,
    override var countryCode: String,
    override var currency: String,
    override var amount: Double,
    var interest: Double,
    val capitalization: SourceManager.Capitalization,
    override var transactions: MutableList<Transaction>
) : Source {
    fun save(context: Context): Boolean {
        return SourceManager.save(
            context, name, "/account", this
        )
    }

    fun recalculate() {
        amount = SourceManager.recalculate(transactions)
    }

    fun delete(context: Context): Boolean {
        return SourceManager.delete(
            context, name, "/account"
        )
    }

    fun update(context: Context): Boolean {
        return SourceManager.update(
            context, name, "/account", this
        )
    }

    companion object {
        // This is safe - I promise
        @Suppress("UNCHECKED_CAST")
        fun get(context: Context): List<SourceAccount> {
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

    override fun toString(): String {
        return "SourceAccount{$name}"
    }
}
