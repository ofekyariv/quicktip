package com.ofekyariv.quicktip.ui

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Android implementation of timestamp formatting.
 */
actual fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
    return formatter.format(date)
}
