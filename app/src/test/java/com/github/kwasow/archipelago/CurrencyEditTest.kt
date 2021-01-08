package com.github.kwasow.archipelago

import com.github.kwasow.archipelago.views.CurrencyEdit
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class CurrencyEditTest {
    @Test
    fun formatDoubleCurrency() {
        assertEquals(
            "zł25,809.63",
            CurrencyEdit.formatDouble(25809.63, "zł")
        )
    }

    @Test
    fun formatDoubleNoCurrency() {
        assertEquals(
            "1,435,230.00",
            CurrencyEdit.formatDouble(1435230.00)
        )
    }

    @Test
    fun formatBigDecimalCurrency() {
        val bigDecimal = BigDecimal(983654.97)
        assertEquals(
            "zł983,654.97",
            CurrencyEdit.formatBigDecimal(bigDecimal, "zł")
        )
    }

    @Test
    fun formatBigDecimalNoCurrency() {
        val bigDecimal = BigDecimal(9854.90)
        assertEquals(
            "9,854.90",
            CurrencyEdit.formatBigDecimal(bigDecimal)
        )
    }
}
