package com.github.kwasow.archipelago

import com.github.kwasow.archipelago.data.Source
import com.github.kwasow.archipelago.data.Transaction
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
                1500.0,
                "None"
            )
        )
        list.add(
            Transaction(
                Date(),
                "Test 1",
                -500.0,
                "None"
            )
        )
        // This date happened a long time ago - shouldn't be counted
        list.add(
            Transaction(
                Date(0),
                "Test 1",
                -200.0,
                "None"
            )
        )
        list.add(
            Transaction(
                Date(),
                "Test 1",
                300.0,
                "None"
            )
        )

        assertEquals(1300.0, Source.getMonthChange(list), 0.0)
    }
}
