package com.ofekyariv.quicktip.analytics

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.crashlytics.crashlytics

/**
 * Wrapper around Firebase Crashlytics for crash reporting.
 * Crashlytics auto-initializes with Firebase — this provides convenience methods.
 */
object CrashReporter {

    /**
     * Log a non-fatal exception to Crashlytics.
     */
    fun recordException(throwable: Throwable) {
        try {
            Firebase.crashlytics.recordException(throwable)
        } catch (_: Exception) {
            // Crashlytics not available (e.g., in tests)
        }
    }

    /**
     * Log a custom message to Crashlytics (visible in crash reports).
     */
    fun log(message: String) {
        try {
            Firebase.crashlytics.log(message)
        } catch (_: Exception) {
            // Crashlytics not available
        }
    }

    /**
     * Set a custom key-value pair for crash reports.
     */
    fun setCustomKey(key: String, value: String) {
        try {
            Firebase.crashlytics.setCustomKey(key, value)
        } catch (_: Exception) {
            // Crashlytics not available
        }
    }

    /**
     * Set the user identifier for crash reports.
     */
    fun setUserId(userId: String) {
        try {
            Firebase.crashlytics.setUserId(userId)
        } catch (_: Exception) {
            // Crashlytics not available
        }
    }
}
