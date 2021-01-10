package com.github.kwasow.archipelago.data

import android.content.Context
import com.github.kwasow.archipelago.utils.SourceManager
import org.json.JSONObject
import java.math.BigDecimal

data class SourceAccount(
    override var name: String,
    override var country: String,
    override var countryCode: String,
    override var currency: String,
    override var amount: BigDecimal,
    var interest: Int,
    val capitalization: Capitalization,
    override var transactions: MutableList<Transaction>
) : Source {

    companion object {
        private const val JSON_INTEREST = "interest"
        private const val JSON_CAPITALIZATION = "capitalization"

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
                returnList.add(fromJsonObject(it))
            }

            return returnList
        }

        private fun fromJsonObject(jsonObject: JSONObject): SourceAccount {
            val genericSource = Source.fromJsonObject(jsonObject)

            // Get account specific
            val jsonInterest = jsonObject.getInt(JSON_INTEREST)
            val jsonCapitalization =
                Capitalization.fromInt(jsonObject.getInt(JSON_CAPITALIZATION))

            return SourceAccount(
                genericSource.name,
                genericSource.country,
                genericSource.countryCode,
                genericSource.currency,
                genericSource.amount,
                jsonInterest,
                jsonCapitalization,
                genericSource.transactions
            )
        }
    }

    fun save(context: Context): Boolean {
        return SourceManager.save(
            context, name, "/account", toJsonObject()
        )
    }

    fun delete(context: Context): Boolean {
        return SourceManager.delete(
            context, name, "/account"
        )
    }

    fun update(context: Context): Boolean {
        return SourceManager.update(
            context, name, "/account", toJsonObject()
        )
    }

    override fun toString(): String {
        return "SourceAccount{$name}"
    }

    override fun toJsonObject(): JSONObject {
        val returnObject = super.toJsonObject()

        // Add specific stuff
        returnObject.put(JSON_INTEREST, interest)
        returnObject.put(JSON_CAPITALIZATION, capitalization.toInt())

        return returnObject
    }
}
