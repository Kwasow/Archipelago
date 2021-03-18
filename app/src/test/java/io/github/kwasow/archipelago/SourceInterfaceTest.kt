package io.github.kwasow.archipelago

import io.github.kwasow.archipelago.data.Source
import io.github.kwasow.archipelago.data.Transaction
import org.javamoney.moneta.Money
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Date

class SourceInterfaceTest {
    @Test
    fun getMonthChange() {
        val list = mutableListOf<Transaction>()
        list.add(
            Transaction(
                Date(),
                "Test 1",
                Money.of(1500.0, "PLN"),
                "None"
            )
        )
        list.add(
            Transaction(
                Date(),
                "Test 1",
                Money.of(-500.0, "PLN"),
                "None"
            )
        )
        // This date happened a long time ago - shouldn't be counted
        list.add(
            Transaction(
                Date(0),
                "Test 1",
                Money.of(-200.0, "PLN"),
                "None"
            )
        )
        list.add(
            Transaction(
                Date(),
                "Test 1",
                Money.of(300.0, "PLN"),
                "None"
            )
        )

        assertEquals(
            "PLN 1300.00",
            Source.getMonthChange(list, "PLN").toString()
        )
    }
}
