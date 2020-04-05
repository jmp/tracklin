package com.github.jmp.tracklin

import java.io.PrintWriter
import java.io.Writer

private const val CSV_SEPARATOR = ";"

/**
 * Export the given hours into the given file.
 *
 * @param writer the writer to write to
 * @param items items to write into the file
 */
fun export(writer: Writer, items: List<Hours>) =
    with(PrintWriter(writer)) {
        items.forEach { item ->
            print(item.startTime)
            print(CSV_SEPARATOR)
            print(item.endTime)
            print(CSV_SEPARATOR)
            print(item.task)
            println()
        }
    }
