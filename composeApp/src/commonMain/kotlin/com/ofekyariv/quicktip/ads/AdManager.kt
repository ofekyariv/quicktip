package com.ofekyariv.quicktip.ads

/**
 * Common interface for managing advertisements across platforms.
 * All ad methods check RemoteAdConfig before loading/showing.
 */
expect class AdManager {
    /** Initialize the AdMob SDK and fetch Remote Config. */
    fun initialize()

    /** Load an interstitial ad (checks RemoteAdConfig). */
    fun loadInterstitialAd()

    /** Show a loaded interstitial ad. Returns true if shown. */
    fun showInterstitialAd(): Boolean

    /** Load a rewarded ad (checks RemoteAdConfig). */
    fun loadRewardedAd()

    /** Show a loaded rewarded ad. Returns true if shown. */
    fun showRewardedAd(onRewarded: () -> Unit): Boolean

    /** Get the ad unit ID for banner ads (from RemoteAdConfig or BuildConfig). */
    fun getBannerAdUnitId(): String

    /** Load an app open ad (checks RemoteAdConfig). */
    fun loadAppOpenAd()

    /** Show a loaded app open ad with cooldown. Returns true if shown. */
    fun showAppOpenAd(): Boolean

    /** Fetch remote ad config. Call on app start. */
    suspend fun fetchRemoteConfig()
}
