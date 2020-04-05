package com.github.jmp.tracklin

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.StringWriter

class ExportTest {
    private fun StringWriter.normalizedContent() =
        toString()
            .replace("\r\n", "\n")
            .replace("\r", "\n")

    @Test
    fun `Calling write with empty hours should not write any lines to the writer`() {
        val writer = StringWriter()
        val hours = ArrayList<Hours>()
        export(writer, hours)
        assertEquals("", writer.normalizedContent())
    }

    @Test
    fun `Calling write with non-empty hours should write an equal number of lines to the writer`() {
        val writer = StringWriter()
        val hours = ArrayList<Hours>()
        hours.add(Hours("11:22:33", "11:22:44", "TASK-1"))
        hours.add(Hours("22:33:44", "22:33:55", "TASK-2"))
        export(writer, hours)
        assertEquals(
            "11:22:33;11:22:44;TASK-1\n22:33:44;22:33:55;TASK-2\n",
            writer.normalizedContent()
        )
    }
}
