package io.github.kwasow.archipelago.data

import android.content.Context
import org.javamoney.moneta.Money
import org.json.JSONObject
import java.util.Date

data class SourceInvestment(
    override var name: String,
    override var country: String,
    override var countryCode: String,
    override var currencyCode: String,
    override var sum: Money,
    var interest: Int,
    val capitalization: Capitalization,
    var start: Date,
    var end: Date
) : Source {
    override var transactions: MutableList<Transaction> = mutableListOf()

    companion object {
        private const val JSON_INTEREST = "interest"
        private const val JSON_CAPITALIZATION = "capitalization"
        private const val JSON_DATE_START = "start"
        private const val JSON_DATE_END = "end"

        fun get(context: Context): List<SourceInvestment> {
            val anyList = Source.get(
                context, "/investment"
            )

            // Return empty list if empty
            if (anyList.isEmpty()) return listOf()

            val returnList = mutableListOf<SourceInvestment>()
            anyList.forEach {
                returnList.add(fromJsonObject(it))
            }

            return returnList
        }

        private fun fromJsonObject(jsonObject: JSONObject): SourceInvestment {
            val genericSource = Source.fromJsonObject(jsonObject)

            // Get investment specific
            val jsonInterest = jsonObject.getInt(JSON_INTEREST)
            val jsonCapitalization =
                Capitalization.fromInt(jsonObject.getInt(JSON_CAPITALIZATION))
            val jsonStart = jsonObject.getLong(JSON_DATE_START)
            val jsonEnd = jsonObject.getLong(JSON_DATE_END)

            return SourceInvestment(
                genericSource.name,
                genericSource.country,
                genericSource.countryCode,
                genericSource.currencyCode,
                genericSource.sum,
                jsonInterest,
                jsonCapitalization,
                Date(jsonStart),
                Date(jsonEnd)
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
        return "SourceInvestment{$name}"
    }

    override fun toJsonObject(): JSONObject {
        val returnObject = super.toJsonObject()

        // Add specific stuff
        returnObject.put(JSON_INTEREST, interest)
        returnObject.put(JSON_CAPITALIZATION, capitalization.toInt())
        returnObject.put(JSON_DATE_START, start.time)
        returnObject.put(JSON_DATE_END, end.time)

        return returnObject
    }

    // We don't want this to do anything
    override fun recalculate() {
        val saveAmount = sum
        super.recalculate()
        sum = saveAmount
    }
}
