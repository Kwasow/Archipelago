package com.github.kwasow.archipelago.data

import com.github.kwasow.archipelago.R

enum class Capitalization(val value: Int) {
    EndOfMonth(R.string.end_of_month),
    EndOfInvestment(R.string.end_of_investment),
    Monthly(R.string.monthly),
    Yearly(R.string.yearly);

    fun toInt(): Int {
        return ordinal
    }

    companion object {
        fun fromInt(int: Int): Capitalization {
            return when (int) {
                1 -> EndOfInvestment
                2 -> Monthly
                3 -> Yearly
                else -> EndOfMonth // This includes ordinal = 0
            }
        }
    }
}
