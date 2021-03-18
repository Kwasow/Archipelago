package io.github.kwasow.archipelago

import org.javamoney.moneta.Money
import org.junit.Assert.assertEquals
import org.junit.Test

class JavaMoneyTest {

    @Test
    fun sumMoney() {
        val money1 = Money.of(345.23, "PLN")
        val money2 = Money.of(375.23, "PLN")
        val sum = money1.add(money2)

        assertEquals("PLN 720.46", sum.toString())
    }

    @Test
    fun sumNegative() {
        val money1 = Money.of(-345.23, "PLN")
        val money2 = Money.of(375.23, "PLN")
        val sum = money1.add(money2)

        assertEquals("PLN 30.00", sum.toString())
    }
}
