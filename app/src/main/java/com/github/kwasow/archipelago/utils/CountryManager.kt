package com.github.kwasow.archipelago.utils

import android.content.Context
import com.github.kwasow.archipelago.data.Country
import com.github.kwasow.archipelago.data.Tax
import org.json.JSONObject
import java.io.InputStream
import java.nio.charset.Charset

class CountryManager {
    companion object {
        private const val JSON_SAVINGS_TAX = "savings"
        private const val JSON_INVESTMENT_TAX = "investment"
        private const val JSON_STOCK_TAX = "stock"

        fun getCountries(context: Context): Array<Country> {
            val list = mutableListOf<Country>()

            context.assets.list("countries/")?.forEach {
                list.add(
                    getCountry(context, "countries/$it")
                )
            }

            return list.toTypedArray()
        }

        fun getCountyByCode(context: Context, code: String): Country {
            return getCountry(context, "countries/$code.json")
        }

        private fun getCountry(context: Context, filePath: String): Country {
            val inputStream = context.assets.open(filePath)
            val jsonString = inputStream.readTextAndClose()
            val jsonObject = JSONObject(jsonString)

            val taxAccount = getTaxBrackets(jsonObject, JSON_SAVINGS_TAX)
            val taxInvestment = getTaxBrackets(jsonObject, JSON_INVESTMENT_TAX)
            val taxStock = getTaxBrackets(jsonObject, JSON_STOCK_TAX)

            return Country(
                jsonObject.getString("full name"),
                jsonObject.getString("country code"),
                jsonObject.getString("currency code"),
                taxAccount, taxInvestment, taxStock
            )
        }

        private fun getTaxBrackets(jsonObject: JSONObject, source: String): List<Tax> {
            val list = mutableListOf<Tax>()
            val sourceObject = jsonObject.getJSONObject("taxes").getJSONObject(source)

            val taxed = if (sourceObject.getString("taxed") == "income") {
                Tax.Companion.Taxed.INCOME
            } else {
                Tax.Companion.Taxed.SAVINGS
            }
            val automatic = sourceObject.getBoolean("automatic")

            val bracketsArray = sourceObject.getJSONArray("brackets")
            for (i in 0 until bracketsArray.length()) {
                val bracketDetails = bracketsArray.getJSONArray(i)

                list.add(
                    Tax(
                        taxed,
                        automatic,
                        bracketDetails.getInt(0),
                        bracketDetails.getInt(1),
                        bracketDetails.getInt(2)
                    )
                )
            }

            return list
        }
    }
}

private fun InputStream.readTextAndClose(charset: Charset = Charsets.UTF_8): String {
    return this.bufferedReader(charset).use { it.readText() }
}
