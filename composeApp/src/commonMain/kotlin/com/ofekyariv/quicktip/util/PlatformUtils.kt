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

/**
 * Get the device locale's country code (e.g., "US", "IL", "JP").
 * Returns "US" as fallback if locale cannot be determined.
 */
expect fun getDeviceLocaleCountryCode(): String
