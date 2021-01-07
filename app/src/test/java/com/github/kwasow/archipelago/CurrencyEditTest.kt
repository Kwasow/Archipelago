package com.github.kwasow.archipelago

import com.github.kwasow.archipelago.views.CurrencyEdit
import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyEditTest {
    @Test
    fun formatDouble() {
        assertEquals(
            "zł 25,809.63",
            CurrencyEdit.formatDouble(25809.63, "zł")
        )
    }
}