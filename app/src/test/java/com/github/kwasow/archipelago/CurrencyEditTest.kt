package com.github.kwasow.archipelago

import com.github.kwasow.archipelago.views.CurrencyEdit
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class CurrencyEditTest {

    @Test
    fun formatBigDecimalCurrency() {
        val bigDecimal = BigDecimal(983654.97)
        assertEquals(
            "zł983,654.97",
            CurrencyEdit.formatBigDecimal(bigDecimal, "zł")
        )
    }

}
