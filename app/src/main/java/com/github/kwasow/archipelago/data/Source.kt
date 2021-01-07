package com.github.kwasow.archipelago.data

import java.io.Serializable
import java.util.Calendar
import java.util.Date

interface Source : Serializable {
    var name: String
    var country: String
    var countryCode: String
    var currency: String
    var amount: Double
    var transactions: MutableList<Transaction>

    companion object {
        const val INTENT_PUT_NAME = "intentSourceObjectSerializable"

        fun getMonthChange(transactions: List<Transaction>): Double {
            val calendar = Calendar.getInstance()
            calendar.time = Date()
            // Set calendar to beginning of this month
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            val beginning = calendar.time

            // Calculate change
            var change = 0.0
            transactions.forEach {
                if (it.date.after(beginning)) {
                    change += it.amount
                }
            }

            return change
        }
    }
}