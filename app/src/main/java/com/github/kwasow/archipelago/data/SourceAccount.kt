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
        var transactions: Array<Transaction>
) : Serializable {
    fun save(context: Context): Boolean {
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

    fun update(context: Context) {
        SourceManager.update(
                context, name, "/account", this
        )
    }

    companion object {
        // This is safe - I promise
        @Suppress("UNCHECKED_CAST")
        fun get(context: Context): Array<SourceAccount> {
            val anyArray = SourceManager.get(
                    context, "/account"
            )

            // Return empty array if empty
            if (anyArray.isEmpty()) return arrayOf()

            val returnArray = mutableListOf<SourceAccount>()
            anyArray.forEach {
                if (it is SourceAccount) {
                    returnArray.add(it)
                }
            }

            return returnArray.toTypedArray()
        }
    }

    override fun toString(): String {
        return "SourceAccount{$name}"
    }

    // Generated automatically
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SourceAccount

        if (name != other.name) return false
        if (country != other.country) return false
        if (countryCode != other.countryCode) return false
        if (currency != other.currency) return false
        if (amount != other.amount) return false
        if (interest != other.interest) return false
        if (capitalization != other.capitalization) return false
        if (!transactions.contentEquals(other.transactions)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + country.hashCode()
        result = 31 * result + countryCode.hashCode()
        result = 31 * result + currency.hashCode()
        result = 31 * result + amount.hashCode()
        result = 31 * result + interest.hashCode()
        result = 31 * result + capitalization.hashCode()
        result = 31 * result + transactions.contentHashCode()
        return result
    }
}