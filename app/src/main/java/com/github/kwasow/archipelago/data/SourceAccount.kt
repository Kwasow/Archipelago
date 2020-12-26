package com.github.kwasow.archipelago.data

import android.content.Context
import com.github.kwasow.archipelago.utils.SourceManager

data class SourceAccount(
        var name: String,
        var country: String,
        var countryCode: String,
        var currency: String,
        var amount: Double,
        var interest: Double,
        val capitalization: SourceManager.Capitalization,
        var transactions: Array<Transaction>
) {
    fun save(context: Context): Boolean {
        return SourceManager.save(
                context, name, "/account", this
        )
    }

    fun recalculate() {
        amount = SourceManager.recalculate(transactions)
    }

    companion object {
        // This is safe - I promise
        @Suppress("UNCHECKED_CAST")
        fun get(context: Context): Array<SourceAccount> {
            return SourceManager.get(
                    context, "/account"
            ) as Array<SourceAccount>
        }
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