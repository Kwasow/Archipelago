package com.github.kwasow.archipelago.data

import org.javamoney.moneta.Money
import org.json.JSONObject
import java.io.Serializable
import java.util.Date

data class Transaction(
    var date: Date,
    var name: String,
    var amount: Money,
    var details: String
) : Serializable {

    companion object {
        private const val JSON_DATE = "date"
        private const val JSON_NAME = "name"
        private const val JSON_AMOUNT = "amount"
        private const val JSON_DETAILS = "details"

        fun fromJsonObject(jsonObject: JSONObject, currencyCode: String): Transaction {
            val jsonDate = jsonObject.getLong(JSON_DATE)
            val jsonName = jsonObject.getString(JSON_NAME)
            val jsonAmount = Money.of(jsonObject.get(JSON_AMOUNT) as Number, currencyCode)
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
        returnObject.put(JSON_AMOUNT, amount.number)
        returnObject.put(JSON_DETAILS, details)

        return returnObject
    }
}
