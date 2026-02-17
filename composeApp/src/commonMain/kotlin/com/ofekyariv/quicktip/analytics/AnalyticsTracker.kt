package com.ofekyariv.quicktip.analytics

/**
 * Analytics tracker for logging user events via Firebase Analytics.
 * Uses expect/actual pattern for platform-specific Firebase implementations.
 */
expect class AnalyticsTracker() {
    fun logEvent(name: String, params: Map<String, Any>)
}

// Standard event tracking extensions
fun AnalyticsTracker.trackAppOpen() {
    logEvent("app_open", emptyMap())
}

fun AnalyticsTracker.trackCalculationPerformed(currency: String, tipPercent: Double, numberOfPeople: Int) {
    logEvent("calculation_performed", mapOf(
        "currency" to currency,
        "tip_percentage" to tipPercent,
        "number_of_people" to numberOfPeople
    ))
}

fun AnalyticsTracker.trackCalculationSaved(billAmount: Double, total: Double) {
    logEvent("calculation_saved", mapOf(
        "bill_amount" to billAmount,
        "total_amount" to total
    ))
}

fun AnalyticsTracker.trackHistoryViewed(calculationCount: Int) {
    logEvent("history_viewed", mapOf(
        "calculation_count" to calculationCount
    ))
}

fun AnalyticsTracker.trackPremiumViewed(source: String) {
    logEvent("premium_viewed", mapOf(
        "source" to source
    ))
}

fun AnalyticsTracker.trackPremiumPurchased(price: Double = 1.99) {
    logEvent("premium_purchased", mapOf(
        "value" to price,
        "currency" to "USD"
    ))
}

fun AnalyticsTracker.trackPremiumRestored() {
    logEvent("premium_restored", emptyMap())
}

fun AnalyticsTracker.trackAdInterstitialShown() {
    logEvent("ad_interstitial_shown", emptyMap())
}

fun AnalyticsTracker.trackAdRewardedWatched() {
    logEvent("ad_rewarded_watched", emptyMap())
}

fun AnalyticsTracker.trackRewardedUnlockActivated(durationHours: Int = 24) {
    logEvent("rewarded_unlock_activated", mapOf(
        "duration_hours" to durationHours
    ))
}

fun AnalyticsTracker.trackCurrencyChanged(oldCurrency: String, newCurrency: String) {
    logEvent("currency_changed", mapOf(
        "old_currency" to oldCurrency,
        "new_currency" to newCurrency
    ))
}

fun AnalyticsTracker.trackTipPresetUsed(percentage: Double) {
    logEvent("tip_preset_used", mapOf(
        "percentage" to percentage
    ))
}

fun AnalyticsTracker.trackCustomTipEntered(percentage: Double) {
    logEvent("custom_tip_entered", mapOf(
        "percentage" to percentage
    ))
}

fun AnalyticsTracker.trackSplitChanged(numberOfPeople: Int) {
    logEvent("split_changed", mapOf(
        "number_of_people" to numberOfPeople
    ))
}

fun AnalyticsTracker.trackSettingsOpened() {
    logEvent("settings_opened", emptyMap())
}

fun AnalyticsTracker.trackThemeChanged(theme: String) {
    logEvent("theme_changed", mapOf(
        "theme" to theme
    ))
}

fun AnalyticsTracker.trackRoundingRuleChanged(rule: String) {
    logEvent("rounding_rule_changed", mapOf(
        "rule" to rule
    ))
}

fun AnalyticsTracker.trackHistoryCleared() {
    logEvent("history_cleared", emptyMap())
}

fun AnalyticsTracker.trackCalculationDeleted() {
    logEvent("calculation_deleted", emptyMap())
}

fun AnalyticsTracker.trackHistoryItemDeleted() {
    logEvent("history_item_deleted", emptyMap())
}

fun AnalyticsTracker.trackErrorOccurred(errorType: String, errorMessage: String) {
    logEvent("error_occurred", mapOf(
        "error_type" to errorType,
        "error_message" to errorMessage
    ))
}
