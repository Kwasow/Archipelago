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
        var transactions: Array<Transaction>
) : Serializable {
    fun save(context: Context): Boolean {
        return SourceManager.save(
                context, name, "/cash", this
        )
    }

    fun recalculate() {
        amount = SourceManager.recalculate(transactions)
    }

    companion object {
        // This is safe - I promise
        @Suppress("UNCHECKED_CAST")
        fun get(context: Context): Array<SourceCash> {
            return SourceManager.get(
                    context, "/cash"
            ) as Array<SourceCash>
        }
    }

    // Generated automatically
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SourceCash

        if (name != other.name) return false
        if (country != other.country) return false
        if (countryCode != other.countryCode) return false
        if (currency != other.currency) return false
        if (amount != other.amount) return false
        if (!transactions.contentEquals(other.transactions)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + country.hashCode()
        result = 31 * result + countryCode.hashCode()
        result = 31 * result + currency.hashCode()
        result = 31 * result + amount.hashCode()
        result = 31 * result + transactions.contentHashCode()
        return result
    }
}