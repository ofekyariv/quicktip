package com.ofekyariv.quicktip.ui

import platform.Foundation.*

/**
 * iOS implementation of timestamp formatting.
 */
actual fun formatTimestamp(timestamp: Long): String {
    val timeInterval = timestamp.toDouble() / 1000.0
    val date = NSDate.dateWithTimeIntervalSince1970(timeInterval)
    val formatter = NSDateFormatter()
    formatter.setDateFormat("MMM d, h:mm a")
    return formatter.stringFromDate(date)
}
