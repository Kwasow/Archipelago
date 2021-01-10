package com.github.kwasow.archipelago.data

import org.json.JSONObject
import java.io.Serializable
import java.math.BigDecimal
import java.util.Date

data class Transaction(
    var date: Date,
    var name: String,
    var amount: BigDecimal,
    var details: String
) : Serializable {

    companion object {
        private const val JSON_DATE = "date"
        private const val JSON_NAME = "name"
        private const val JSON_AMOUNT = "amount"
        private const val JSON_DETAILS = "details"

        fun fromJsonObject(jsonObject: JSONObject): Transaction {
            val jsonDate = jsonObject.getLong(JSON_DATE)
            val jsonName = jsonObject.getString(JSON_NAME)
            val jsonAmount = BigDecimal(jsonObject.getString(JSON_AMOUNT))
            val jsonDetails = jsonObject.getString(JSON_DETAILS)

            return Transaction(
                Date(jsonDate),
                jsonName,
                jsonAmount,
                jsonDetails
            )
        }
    }

    fun toJsonObject(): JSONObject {
        val returnObject = JSONObject()

        returnObject.put(JSON_DATE, date.time)
        returnObject.put(JSON_NAME, name)
        returnObject.put(JSON_AMOUNT, amount.toString())
        returnObject.put(JSON_DETAILS, details)

        return returnObject
    }
}
