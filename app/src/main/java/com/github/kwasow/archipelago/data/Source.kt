package com.github.kwasow.archipelago.data

import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable
import java.math.BigDecimal
import java.util.Calendar
import java.util.Date

interface Source : Serializable {
    var name: String
    var country: String
    var countryCode: String
    var currency: String
    var amount: BigDecimal
    var transactions: MutableList<Transaction>

    companion object {
        const val INTENT_PUT_NAME = "intentSourceObjectSerializable"

        const val JSON_NAME = "name"
        const val JSON_COUNTRY = "country"
        const val JSON_COUNTRY_CODE = "countryCode"
        const val JSON_CURRENCY = "currency"
        const val JSON_AMOUNT = "amount"
        const val JSON_TRANSACTIONS = "transactions"

        fun getMonthChange(transactions: List<Transaction>): BigDecimal {
            val calendar = Calendar.getInstance()
            calendar.time = Date()
            // Set calendar to beginning of this month
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            val beginning = calendar.time

            // Calculate change
            var change = BigDecimal(0)
            transactions.forEach {
                if (it.date.after(beginning)) {
                    change += it.amount
                }
            }

            return change
        }

        fun fromJsonObject(jsonObject: JSONObject): Source {
            // Get common details
            val jsonName = jsonObject.getString(JSON_NAME)
            val jsonCountry = jsonObject.getString(JSON_COUNTRY)
            val jsonCountryCode = jsonObject.getString(JSON_COUNTRY_CODE)
            val jsonCurrency = jsonObject.getString(JSON_CURRENCY)
            val jsonAmount = BigDecimal(
                jsonObject.getString(JSON_AMOUNT)
            )

            // Get transactions
            val jsonTransactions = mutableListOf<Transaction>()
            for (i in 0 until jsonObject.getJSONArray(JSON_TRANSACTIONS).length()) {
                val transactionObject = jsonObject.getJSONArray(JSON_TRANSACTIONS)[i] as JSONObject
                jsonTransactions.add(
                    Transaction.fromJsonObject(transactionObject)
                )
            }

            // Return anything that implements interface - SourceCash is the simplest
            return SourceCash(
                jsonName,
                jsonCountry,
                jsonCountryCode,
                jsonCurrency,
                jsonAmount,
                jsonTransactions
            )
        }
    }

    fun recalculate() {
        var sum = BigDecimal(0)
        amount = BigDecimal(0)

        transactions.forEach {
            amount += it.amount
        }
    }

    fun toJsonObject(): JSONObject {
        val returnObject = JSONObject()

        // Put common details
        returnObject.put(JSON_NAME, name)
        returnObject.put(JSON_COUNTRY, country)
        returnObject.put(JSON_COUNTRY_CODE, countryCode)
        returnObject.put(JSON_CURRENCY, currency)
        returnObject.put(JSON_AMOUNT, amount.toString())

        // Generate transactions array
        val transactionsArray = JSONArray()
        transactions.forEach {
            transactionsArray.put(it.toJsonObject())
        }

        returnObject.put(JSON_TRANSACTIONS, transactionsArray)

        // Return the common stuff
        return returnObject
    }
}
