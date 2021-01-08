package com.github.kwasow.archipelago.data

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

    fun recalculate() {
        var sum = BigDecimal(0)
        amount = BigDecimal(0)

        transactions.forEach {
            amount += it.amount
        }
    }

    companion object {
        const val INTENT_PUT_NAME = "intentSourceObjectSerializable"

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
    }
}
