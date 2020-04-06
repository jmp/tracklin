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
        items.forEach {
            print(it.startTime)
            print(CSV_SEPARATOR)
            print(it.endTime)
            print(CSV_SEPARATOR)
            print(it.task)
            println()
        }
    }
