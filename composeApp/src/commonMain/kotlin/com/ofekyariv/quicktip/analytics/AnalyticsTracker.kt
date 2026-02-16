package com.ofekyariv.quicktip.analytics

/**
 * Analytics tracker for logging user events.
 * Currently logs to console. Will be connected to Firebase Analytics in production.
 * 
 * TODO: Integrate with Firebase Analytics when project credentials are configured.
 * For now, this provides the structure and logging for development/testing.
 */
class AnalyticsTracker {

    /**
     * Log a custom event with optional parameters.
     */
    fun logEvent(name: String, params: Map<String, Any> = emptyMap()) {
        try {
            // Log to console for development
            val paramsString = if (params.isNotEmpty()) {
                params.entries.joinToString(", ") { "${it.key}=${it.value}" }
            } else {
                "no params"
            }
            println("[Analytics] Event: $name | $paramsString")
            
            // TODO: When Firebase is configured, add:
            // Firebase.analytics.logEvent(name, params)
        } catch (e: Exception) {
            // Silently fail - analytics should never crash the app
            println("[Analytics] Error logging event '$name': ${e.message}")
        }
    }

    // Standard events
    fun trackAppOpen() {
        logEvent("app_open")
    }

    fun trackCalculationPerformed(currency: String, tipPercent: Double, numberOfPeople: Int) {
        logEvent("calculation_performed", mapOf(
            "currency" to currency,
            "tip_percentage" to tipPercent,
            "number_of_people" to numberOfPeople
        ))
    }

    fun trackCalculationSaved(billAmount: Double, total: Double) {
        logEvent("calculation_saved", mapOf(
            "bill_amount" to billAmount,
            "total_amount" to total
        ))
    }

    fun trackHistoryViewed(calculationCount: Int) {
        logEvent("history_viewed", mapOf(
            "calculation_count" to calculationCount
        ))
    }

    fun trackPremiumViewed(source: String) {
        logEvent("premium_viewed", mapOf(
            "source" to source
        ))
    }

    fun trackPremiumPurchased(price: Double = 0.99) {
        logEvent("premium_purchased", mapOf(
            "value" to price,
            "currency" to "USD"
        ))
    }

    fun trackPremiumRestored() {
        logEvent("premium_restored")
    }

    fun trackAdInterstitialShown() {
        logEvent("ad_interstitial_shown")
    }

    fun trackAdRewardedWatched() {
        logEvent("ad_rewarded_watched")
    }

    fun trackRewardedUnlockActivated(durationHours: Int = 24) {
        logEvent("rewarded_unlock_activated", mapOf(
            "duration_hours" to durationHours
        ))
    }

    fun trackCurrencyChanged(oldCurrency: String, newCurrency: String) {
        logEvent("currency_changed", mapOf(
            "old_currency" to oldCurrency,
            "new_currency" to newCurrency
        ))
    }

    fun trackTipPresetUsed(percentage: Double) {
        logEvent("tip_preset_used", mapOf(
            "percentage" to percentage
        ))
    }

    fun trackCustomTipEntered(percentage: Double) {
        logEvent("custom_tip_entered", mapOf(
            "percentage" to percentage
        ))
    }

    fun trackSplitChanged(numberOfPeople: Int) {
        logEvent("split_changed", mapOf(
            "number_of_people" to numberOfPeople
        ))
    }

    fun trackSettingsOpened() {
        logEvent("settings_opened")
    }

    fun trackThemeChanged(theme: String) {
        logEvent("theme_changed", mapOf(
            "theme" to theme
        ))
    }

    fun trackRoundingRuleChanged(rule: String) {
        logEvent("rounding_rule_changed", mapOf(
            "rule" to rule
        ))
    }

    fun trackHistoryCleared() {
        logEvent("history_cleared")
    }

    fun trackCalculationDeleted() {
        logEvent("calculation_deleted")
    }

    fun trackErrorOccurred(errorType: String, errorMessage: String) {
        logEvent("error_occurred", mapOf(
            "error_type" to errorType,
            "error_message" to errorMessage
        ))
    }
}
