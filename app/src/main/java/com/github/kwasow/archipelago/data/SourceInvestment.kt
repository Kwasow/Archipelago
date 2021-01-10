package com.github.kwasow.archipelago.data

import android.content.Context
import com.github.kwasow.archipelago.utils.SourceManager
import org.json.JSONObject
import java.math.BigDecimal
import java.util.Date

data class SourceInvestment(
    override var name: String,
    override var country: String,
    override var countryCode: String,
    override var currency: String,
    override var amount: BigDecimal,
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

        // This is safe - I promise
        @Suppress("UNCHECKED_CAST")
        fun get(context: Context): List<SourceInvestment> {
            val anyList = SourceManager.get(
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
                genericSource.currency,
                genericSource.amount,
                jsonInterest,
                jsonCapitalization,
                Date(jsonStart),
                Date(jsonEnd)
            )
        }
    }

    fun save(context: Context): Boolean {
        return SourceManager.save(
            context, name, "/investment", toJsonObject()
        )
    }

    fun delete(context: Context): Boolean {
        return SourceManager.delete(
            context, name, "/investment"
        )
    }

    fun update(context: Context): Boolean {
        return SourceManager.update(
            context, name, "/investment", toJsonObject()
        )
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
        val saveAmount = amount
        super.recalculate()
        amount = saveAmount
    }
}
