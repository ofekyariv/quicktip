package com.ofekyariv.quicktip.ads

import android.app.Activity
import android.content.Context
import com.ofekyariv.quicktip.BuildConfig
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

/**
 * Android implementation of AdManager using Google Mobile Ads SDK.
 * All ad methods check RemoteAdConfig first; falls back to BuildConfig IDs
 * when Remote Config IDs are empty.
 */
actual class AdManager(private val context: Context) {
    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private var appOpenAd: AppOpenAd? = null
    private var lastAppOpenShowTime: Long = 0L

    /** Resolve ad ID: Remote Config overrides BuildConfig */
    private fun resolveInterstitialId(): String {
        val remoteId = RemoteAdConfig.interstitialAdId
        return remoteId.ifEmpty { BuildConfig.INTERSTITIAL_AD_UNIT_ID }
    }

    private fun resolveRewardedId(): String {
        val remoteId = RemoteAdConfig.rewardedAdId
        return remoteId.ifEmpty { BuildConfig.REWARDED_AD_UNIT_ID }
    }

    private fun resolveBannerId(): String {
        val remoteId = RemoteAdConfig.bannerAdId
        return remoteId.ifEmpty { BuildConfig.BANNER_AD_UNIT_ID }
    }

    private fun resolveAppOpenId(): String {
        val remoteId = RemoteAdConfig.appOpenAdId
        return remoteId.ifEmpty { BuildConfig.APP_OPEN_AD_UNIT_ID }
    }

    actual fun initialize() {
        MobileAds.initialize(context) {}
    }

    actual suspend fun fetchRemoteConfig() {
        RemoteAdConfig.fetch()
    }

    // ─── Interstitial ─────────────────────────────────────────────

    actual fun loadInterstitialAd() {
        if (!RemoteAdConfig.shouldShowInterstitial() && !RemoteAdConfig.adsEnabled.not()) {
            // If remote config explicitly says no, skip. But if config not fetched yet, allow BuildConfig fallback.
        }
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            resolveInterstitialId(),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) { interstitialAd = ad }
                override fun onAdFailedToLoad(error: com.google.android.gms.ads.LoadAdError) { interstitialAd = null }
            }
        )
    }

    actual fun showInterstitialAd(): Boolean {
        if (!RemoteAdConfig.adsEnabled) return false
        return if (interstitialAd != null && context is Activity) {
            interstitialAd?.show(context)
            interstitialAd = null
            loadInterstitialAd()
            true
        } else false
    }

    // ─── Rewarded ─────────────────────────────────────────────────

    actual fun loadRewardedAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            resolveRewardedId(),
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) { rewardedAd = ad }
                override fun onAdFailedToLoad(error: com.google.android.gms.ads.LoadAdError) { rewardedAd = null }
            }
        )
    }

    actual fun showRewardedAd(onRewarded: () -> Unit): Boolean {
        if (!RemoteAdConfig.adsEnabled) return false
        return if (rewardedAd != null && context is Activity) {
            rewardedAd?.show(context) { _ -> onRewarded() }
            rewardedAd = null
            loadRewardedAd()
            true
        } else false
    }

    // ─── Banner ───────────────────────────────────────────────────

    actual fun getBannerAdUnitId(): String {
        if (!RemoteAdConfig.shouldShowBanner()) return ""
        return resolveBannerId()
    }

    // ─── App Open ─────────────────────────────────────────────────

    actual fun loadAppOpenAd() {
        if (!RemoteAdConfig.shouldShowAppOpen()) return
        val adRequest = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            resolveAppOpenId(),
            adRequest,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) { appOpenAd = ad }
                override fun onAdFailedToLoad(error: com.google.android.gms.ads.LoadAdError) { appOpenAd = null }
            }
        )
    }

    actual fun showAppOpenAd(): Boolean {
        if (!RemoteAdConfig.shouldShowAppOpen()) return false
        val now = System.currentTimeMillis()
        val cooldownMs = RemoteAdConfig.appOpenCooldownMinutes * 60 * 1000
        if (now - lastAppOpenShowTime < cooldownMs) return false

        return if (appOpenAd != null && context is Activity) {
            appOpenAd?.show(context)
            appOpenAd = null
            lastAppOpenShowTime = now
            loadAppOpenAd()
            true
        } else false
    }
}
