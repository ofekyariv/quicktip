package com.ofekyariv.quicktip.ads

/**
 * iOS implementation of AdManager.
 * Stub implementations — AdMob is Android-specific in this version.
 */
actual class AdManager {
    actual fun initialize() {}
    actual fun loadInterstitialAd() {}
    actual fun showInterstitialAd(): Boolean = false
    actual fun loadRewardedAd() {}
    actual fun showRewardedAd(onRewarded: () -> Unit): Boolean = false
    actual fun getBannerAdUnitId(): String = ""
    actual fun loadAppOpenAd() {}
    actual fun showAppOpenAd(): Boolean = false
    actual suspend fun fetchRemoteConfig() {
        RemoteAdConfig.fetch()
    }
}
