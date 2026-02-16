package com.ofekyariv.quicktip.ads

/**
 * Common interface for managing advertisements across platforms.
 * Provides methods to show banner, interstitial, and rewarded ads.
 */
expect class AdManager {
    /**
     * Initialize the AdMob SDK.
     * Should be called once at app startup.
     */
    fun initialize()

    /**
     * Load an interstitial ad.
     */
    fun loadInterstitialAd()

    /**
     * Show a loaded interstitial ad.
     * @return true if ad was shown, false if not loaded yet
     */
    fun showInterstitialAd(): Boolean

    /**
     * Load a rewarded ad.
     */
    fun loadRewardedAd()

    /**
     * Show a loaded rewarded ad.
     * @param onRewarded Callback invoked when user earns reward
     * @return true if ad was shown, false if not loaded yet
     */
    fun showRewardedAd(onRewarded: () -> Unit): Boolean

    /**
     * Get the ad unit ID for banner ads.
     */
    fun getBannerAdUnitId(): String
}
