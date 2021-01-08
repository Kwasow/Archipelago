package com.github.kwasow.archipelago

import com.github.kwasow.archipelago.views.CurrencyEdit
import org.junit.Assert.assertEquals
import org.junit.Test

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
}