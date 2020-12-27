package com.github.kwasow.archipelago.views

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText
import java.text.NumberFormat
import kotlin.math.roundToInt

/**
 * Created by AbhinayMe on 01/01/2019.
 *
 * Converted to Kotlin, modified and maintained by Kwasow since 25/12/2020
 */

class CurrencyEdit : TextInputEditText {
    private var current = ""
    private val editText = this

    // Properties
    private var currency = ""

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence, i: Int, i1: Int, i2: Int) {
                if (s.toString() != current) {
                    editText.removeTextChangedListener(this)

                    val cleanString = s.toString()
                        .replace("[$,.]".toRegex(), "")
                        .replace(currency.toRegex(), "")
                        .replace("\\s+".toRegex(), "")

                    if (cleanString.isNotEmpty()) {
                        val currencyFormat = "$currency "
                        val formatted: String
                        val parsed: Double = cleanString.toDouble()
                        formatted = NumberFormat.getCurrencyInstance().format(
                            parsed / 100
                        ).replace(
                            NumberFormat.getCurrencyInstance().currency.symbol,
                            currencyFormat
                        )
                        current = formatted

                        editText.setText(formatted)
                        editText.setSelection(formatted.length)
                    }

                    editText.addTextChangedListener(this)
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        // To prevent errors with empty strings
        setText("0")
    }

    private fun refresh() {
        val value = getDoubleValue()
        setText(value.toString())
    }

    fun getDoubleValue() : Double {
        return editText.text.toString().trim { it <= ' ' }
            .replace("[$,]".toRegex(), "").replace(currency.toRegex(), "").toDouble()
    }

    // This will fail if value exceeds integer limit
    fun getIntValue() : Int = getDoubleValue().roundToInt()

    fun setCurrency(currencySymbol: String) {
        currency = currencySymbol
        // Refresh will add the currency symbol to whatever is written in the textview
        refresh()
    }
}