package com.github.jmp.tracklin

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.Date

class TimingTest {
    @Test
    fun `getCurrentTimeAsString returns the time as a string`() {
        assertEquals(
            "18:53:57",
            getTimeAsString(Date(1586102037247L))
        )
    }
}