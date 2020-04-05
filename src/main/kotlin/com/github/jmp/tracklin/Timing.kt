package com.github.jmp.tracklin

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

/**
 * Get the current time.
 * @return current time as a String in "HH:mm:ss" format
 */
fun getTimeAsString(
    date: Date = Date()
): String = with(Calendar.getInstance()) {
    timeInMillis = date.time
    SimpleDateFormat("HH:mm:ss").format(time)
}
