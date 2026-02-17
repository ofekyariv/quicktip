package com.ofekyariv.quicktip.ads

import android.app.Activity
import android.content.Context
import com.ofekyariv.quicktip.BuildConfig
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

/**
 * Android implementation of AdManager using Google Mobile Ads SDK.
 * Ad unit IDs are loaded from BuildConfig (debug=test IDs, release=real IDs from local.properties).
 */
actual class AdManager(private val context: Context) {
    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null

    companion object {
        // Ad unit IDs from BuildConfig — debug uses test IDs, release uses real IDs
        private val BANNER_AD_UNIT_ID = BuildConfig.BANNER_AD_UNIT_ID
        private val INTERSTITIAL_AD_UNIT_ID = BuildConfig.INTERSTITIAL_AD_UNIT_ID
        private val REWARDED_AD_UNIT_ID = BuildConfig.REWARDED_AD_UNIT_ID
    }

    actual fun initialize() {
        MobileAds.initialize(context) {}
    }

    actual fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: com.google.android.gms.ads.LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    actual fun showInterstitialAd(): Boolean {
        return if (interstitialAd != null && context is Activity) {
            interstitialAd?.show(context)
            interstitialAd = null
            // Load next ad
            loadInterstitialAd()
            true
        } else {
            false
        }
    }

    actual fun loadRewardedAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            REWARDED_AD_UNIT_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                }

                override fun onAdFailedToLoad(error: com.google.android.gms.ads.LoadAdError) {
                    rewardedAd = null
                }
            }
        )
    }

    actual fun showRewardedAd(onRewarded: () -> Unit): Boolean {
        return if (rewardedAd != null && context is Activity) {
            rewardedAd?.show(context) { _ ->
                onRewarded()
            }
            rewardedAd = null
            // Load next ad
            loadRewardedAd()
            true
        } else {
            false
        }
    }

    actual fun getBannerAdUnitId(): String = BANNER_AD_UNIT_ID
}
