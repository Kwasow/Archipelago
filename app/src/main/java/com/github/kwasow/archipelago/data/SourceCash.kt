package com.github.kwasow.archipelago.data

import android.content.Context
import com.github.kwasow.archipelago.utils.SourceManager
import org.javamoney.moneta.Money
import org.json.JSONObject

data class SourceCash(
    override var name: String,
    override var country: String,
    override var countryCode: String,
    override var currencyCode: String,
    override var amount: Money,
    override var transactions: MutableList<Transaction>
) : Source {
    fun save(context: Context): Boolean {
        return SourceManager.save(
            context, name, "/cash", toJsonObject()
        )
    }

    fun delete(context: Context): Boolean {
        return SourceManager.delete(
            context, name, "/cash"
        )
    }

    fun update(context: Context): Boolean {
        return SourceManager.update(
            context, name, "/cash", toJsonObject()
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
                returnList.add(fromJsonObject(it))
            }

            return returnList
        }

        private fun fromJsonObject(jsonObject: JSONObject): SourceCash {
            val genericSource = Source.fromJsonObject(jsonObject)

            return genericSource as SourceCash
        }
    }

    override fun toString(): String {
        return "SourceCash{$name}"
    }
}
