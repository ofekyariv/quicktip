package com.ofekyariv.quicktip.util

/**
 * Get current time in milliseconds (platform-specific).
 */
expect fun getCurrentTimeMillis(): Long

/**
 * Trigger haptic feedback (light tap).
 */
expect fun performHapticFeedback()

/**
 * Get the app version string.
 */
expect fun getAppVersion(): String
