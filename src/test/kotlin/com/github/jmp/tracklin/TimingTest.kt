package com.github.jmp.tracklin

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class TimingTest {
    @Test
    fun `getTimeAsString returns the time as a string`() {
        val calendar = Calendar.getInstance().apply {
            set(2000, 1, 1, 12, 0, 0)
        }
        assertEquals(
            "12:00:00",
            getTimeAsString(calendar.time)
        )
    }
}