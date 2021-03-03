package com.github.kwasow.archipelago.data

import org.javamoney.moneta.Money
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
    var currencyCode: String
    var amount: Money
    var transactions: MutableList<Transaction>

    companion object {
        const val INTENT_PUT_NAME = "intentSourceObjectSerializable"

        const val JSON_NAME = "name"
        const val JSON_COUNTRY = "country"
        const val JSON_COUNTRY_CODE = "countryCode"
        const val JSON_CURRENCY = "currency"
        const val JSON_AMOUNT = "amount"
        const val JSON_TRANSACTIONS = "transactions"

        fun getMonthChange(transactions: List<Transaction>, currencyCode: String): Money {
            val calendar = Calendar.getInstance()
            calendar.time = Date()
            // Set calendar to beginning of this month
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            val beginning = calendar.time

            // Calculate change
            var change = Money.of(0, currencyCode)
            transactions.forEach {
                if (it.date.after(beginning)) {
                    change = change.add(it.amount)
                }
            }

            return change
        }

        fun fromJsonObject(jsonObject: JSONObject): Source {
            // Get common details
            val jsonName = jsonObject.getString(JSON_NAME)
            val jsonCountry = jsonObject.getString(JSON_COUNTRY)
            val jsonCountryCode = jsonObject.getString(JSON_COUNTRY_CODE)
            val jsonCurrencyCode = jsonObject.getString(JSON_CURRENCY)
            val jsonAmount = Money.of(BigDecimal(
                jsonObject.getString(JSON_AMOUNT)), jsonCurrencyCode)

            // Get transactions
            val jsonTransactions = mutableListOf<Transaction>()
            for (i in 0 until jsonObject.getJSONArray(JSON_TRANSACTIONS).length()) {
                val transactionObject = jsonObject.getJSONArray(JSON_TRANSACTIONS)[i] as JSONObject
                jsonTransactions.add(
                    Transaction.fromJsonObject(transactionObject, jsonCurrencyCode)
                )
            }

            // Return anything that implements interface - SourceCash is the simplest
            return SourceCash(
                jsonName,
                jsonCountry,
                jsonCountryCode,
                jsonCurrencyCode,
                jsonAmount,
                jsonTransactions
            )
        }
    }

    fun recalculate() {
        amount = Money.of(0, currencyCode)

        transactions.forEach {
            amount = amount.add(it.amount)
        }
    }

    fun toJsonObject(): JSONObject {
        val returnObject = JSONObject()

        // Put common details
        returnObject.put(JSON_NAME, name)
        returnObject.put(JSON_COUNTRY, country)
        returnObject.put(JSON_COUNTRY_CODE, countryCode)
        returnObject.put(JSON_CURRENCY, currencyCode)
        returnObject.put(JSON_AMOUNT, amount.number)

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
