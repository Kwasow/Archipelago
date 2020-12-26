package com.github.kwasow.archipelago.utils

import android.content.Context
import com.github.kwasow.archipelago.data.Country
import com.github.kwasow.archipelago.data.Tax
import org.json.JSONObject
import java.io.InputStream
import java.nio.charset.Charset

class CountryManager {
    companion object {
        fun getCountries(context: Context) : Array<Country> {
            val list = mutableListOf<Country>()

            context.assets.list("countries/")?.forEach {
                list.add(
                        getCountry(context, "countries/$it")
                )
            }

            return list.toTypedArray()
        }

        fun getCountyByCode(context: Context, code: String) : Country {
            return getCountry(context, "countries/$code.json")
        }

        private fun getCountry(context: Context, filePath: String) : Country {
            val inputStream = context.assets.open(filePath)
            val jsonString = inputStream.readTextAndClose()
            val jsonObject = JSONObject(jsonString)

            val taxAccount = getTaxBrackets(jsonObject, "savings")
            val taxInvestment = getTaxBrackets(jsonObject, "investment")
            val taxStock = getTaxBrackets(jsonObject, "stock")

            return Country(
                    jsonObject.getString("full name"),
                    jsonObject.getString("country code"),
                    jsonObject.getString("currency symbol"),
                    taxAccount, taxInvestment, taxStock
            )
        }

        private fun getTaxBrackets(jsonObject: JSONObject, source: String) : Array<Tax>{
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

            return list.toTypedArray()
        }
    }
}

private fun InputStream.readTextAndClose(charset: Charset = Charsets.UTF_8): String {
    return this.bufferedReader(charset).use { it.readText() }
}