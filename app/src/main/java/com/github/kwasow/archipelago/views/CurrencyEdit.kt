package com.github.kwasow.archipelago.views

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.github.kwasow.archipelago.utils.ArchipelagoError
import com.google.android.material.textfield.TextInputEditText
import java.math.BigDecimal
import java.text.NumberFormat
import kotlin.math.roundToInt

/**
 * Created by AbhinayMe on 01/01/2019.
 *
 * Converted to Kotlin, modified and maintained by Kwasow since 25/12/2020
 */

class CurrencyEdit : TextInputEditText {
    private var current = ""
    private var currencyChanged = false

    // Properties
    var currency = ""
        set(value) {
            field = value
            // Refresh will add the currency symbol to whatever is written in the textview
            currencyChanged = true
            setText(current)
        }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
        super(context, attrs, defStyleAttr) {
            init()
        }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence, i: Int, i1: Int, i2: Int) {
                if (s.toString() != current || currencyChanged) {
                    removeTextChangedListener(this)
                    currencyChanged = false

                    var cleanString = s.toString()
                        .replace("[$,.]".toRegex(), "")
                        .replace(currency.toRegex(), "")
                        .replace("\\s+".toRegex(), "")
                    val addDot = cleanString.toCharArray().toMutableList()
                    if (addDot.lastIndex - 1 >= 0) {
                        addDot.add(addDot.lastIndex - 1, '.')
                    }
                    cleanString = String(addDot.toCharArray())

                    if (cleanString.isNotEmpty()) {
                        val parsed = BigDecimal(cleanString)
                        val formatted = formatBigDecimal(parsed, currency)

                        current = formatted

                        setText(formatted)
                        setSelection(formatted.length)
                    }

                    addTextChangedListener(this)
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        // To prevent errors with empty strings
        setText("0")
    }

    fun getBigDecimalValue(): BigDecimal {
        return BigDecimal(
            text.toString()
                .replace("[$,]".toRegex(), "")
                .replace(currency.toRegex(), "")
                .replace("\\s+".toRegex(), "")
        )
    }

    fun getDoubleValue(): Double = getBigDecimalValue().toDouble()

    // This will fail if value exceeds integer limit
    fun getIntValue(): Int = getBigDecimalValue().toInt()

    companion object {

        fun formatDouble(value: Double, currency: String): String {
            return NumberFormat.getCurrencyInstance().format(
                value
            ).replace(
                NumberFormat.getCurrencyInstance().currency?.symbol.orEmpty(),
                currency
            )
        }

        fun formatDouble(value: Double): String {
            return formatDouble(value, "")
        }

        fun formatBigDecimal(value: BigDecimal, currency: String): String {
            return NumberFormat.getCurrencyInstance().format(
                    value
            ).replace(
                    NumberFormat.getCurrencyInstance().currency?.symbol.orEmpty(),
                    currency
            )
        }

        fun formatBigDecimal(value: BigDecimal): String {
            return formatBigDecimal(value, "")
        }
    }
}
