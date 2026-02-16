package com.ofekyariv.quicktip.analytics

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.analytics.analytics

/**
 * iOS implementation of AnalyticsTracker using Firebase Analytics.
 */
actual class AnalyticsTracker actual constructor() {
    private val firebaseAnalytics by lazy { Firebase.analytics }

    actual fun logEvent(name: String, params: Map<String, Any>) {
        try {
            firebaseAnalytics.logEvent(name, params)
        } catch (e: Exception) {
            println("[Analytics] Error logging event '$name': ${e.message}")
        }
    }
}
