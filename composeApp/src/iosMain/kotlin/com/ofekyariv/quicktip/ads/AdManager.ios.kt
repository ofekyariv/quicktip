package com.ofekyariv.quicktip.ads

/**
 * iOS implementation of AdManager.
 * Provides stub implementations since AdMob is Android-specific in this implementation.
 */
actual class AdManager {
    actual fun initialize() {
        // No-op for iOS
    }

    actual fun loadInterstitialAd() {
        // No-op for iOS
    }

    actual fun showInterstitialAd(): Boolean {
        // No-op for iOS
        return false
    }

    actual fun loadRewardedAd() {
        // No-op for iOS
    }

    actual fun showRewardedAd(onRewarded: () -> Unit): Boolean {
        // No-op for iOS
        return false
    }

    actual fun getBannerAdUnitId(): String {
        // Return empty string for iOS
        return ""
    }
}
