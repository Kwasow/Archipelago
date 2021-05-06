package io.github.kwasow.archipelago.data

import android.content.Context
import org.javamoney.moneta.Money
import org.json.JSONObject

data class SourceAccount(
    override var name: String,
    override var country: String,
    override var countryCode: String,
    override var currencyCode: String,
    override var sum: Money,
    var interest: Int,
    val capitalization: Capitalization,
    override var transactions: MutableList<Transaction>
) : Source {

    companion object {
        private const val JSON_INTEREST = "interest"
        private const val JSON_CAPITALIZATION = "capitalization"

        fun get(context: Context): List<SourceAccount> {
            val anyList = Source.get(
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
                genericSource.currencyCode,
                genericSource.sum,
                jsonInterest,
                jsonCapitalization,
                genericSource.transactions
            )
        }
    }

    fun save(context: Context): Boolean {
        return save(context, toJsonObject())
    }

    fun update(context: Context): Boolean {
        return update(context, toJsonObject())
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
