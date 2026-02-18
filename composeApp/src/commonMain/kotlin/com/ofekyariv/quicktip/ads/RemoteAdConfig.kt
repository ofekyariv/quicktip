package com.ofekyariv.quicktip.ads

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.remoteconfig.get
import dev.gitlive.firebase.remoteconfig.remoteConfig
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Manages ad configuration via Firebase Remote Config.
 * All ad display decisions flow through this class.
 *
 * Default behavior: ads disabled, all IDs empty → no ads shown until
 * explicitly enabled from Firebase Console.
 */
object RemoteAdConfig {

    // Remote Config keys
    private const val KEY_ADS_ENABLED = "ads_enabled"
    private const val KEY_BANNER_AD_ID = "banner_ad_id"
    private const val KEY_INTERSTITIAL_AD_ID = "interstitial_ad_id"
    private const val KEY_REWARDED_AD_ID = "rewarded_ad_id"
    private const val KEY_APP_OPEN_AD_ID = "app_open_ad_id"
    private const val KEY_ADMOB_APP_ID = "admob_app_id"
    private const val KEY_AD_FREE_HOURS = "ad_free_hours_after_install"
    private const val KEY_INTERSTITIAL_FREQ = "interstitial_frequency_minutes"
    private const val KEY_APP_OPEN_COOLDOWN = "app_open_cooldown_minutes"

    // Cached values (safe defaults = no ads)
    var adsEnabled: Boolean = false; private set
    var bannerAdId: String = ""; private set
    var interstitialAdId: String = ""; private set
    var rewardedAdId: String = ""; private set
    var appOpenAdId: String = ""; private set
    var admobAppId: String = ""; private set
    var adFreeHoursAfterInstall: Long = 24; private set
    var interstitialFrequencyMinutes: Long = 5; private set
    var appOpenCooldownMinutes: Long = 30; private set

    private var fetched = false
    private val mutex = Mutex()

    /**
     * Fetch remote config from Firebase. Call once on app start.
     * On failure, keeps safe defaults (ads disabled).
     */
    suspend fun fetch() = mutex.withLock {
        if (fetched) return@withLock
        try {
            val config = Firebase.remoteConfig
            // Set in-app defaults
            config.setDefaults(
                KEY_ADS_ENABLED to false,
                KEY_BANNER_AD_ID to "",
                KEY_INTERSTITIAL_AD_ID to "",
                KEY_REWARDED_AD_ID to "",
                KEY_APP_OPEN_AD_ID to "",
                KEY_ADMOB_APP_ID to "",
                KEY_AD_FREE_HOURS to 24L,
                KEY_INTERSTITIAL_FREQ to 5L,
                KEY_APP_OPEN_COOLDOWN to 30L
            )
            config.fetchAndActivate()

            // Cache values
            adsEnabled = config[KEY_ADS_ENABLED]
            bannerAdId = config[KEY_BANNER_AD_ID]
            interstitialAdId = config[KEY_INTERSTITIAL_AD_ID]
            rewardedAdId = config[KEY_REWARDED_AD_ID]
            appOpenAdId = config[KEY_APP_OPEN_AD_ID]
            admobAppId = config[KEY_ADMOB_APP_ID]
            adFreeHoursAfterInstall = config[KEY_AD_FREE_HOURS]
            interstitialFrequencyMinutes = config[KEY_INTERSTITIAL_FREQ]
            appOpenCooldownMinutes = config[KEY_APP_OPEN_COOLDOWN]

            fetched = true
        } catch (_: Exception) {
            // Fetch failed → keep safe defaults (no ads)
        }
    }

    /** Check if a specific ad format should show */
    fun shouldShowBanner(): Boolean = adsEnabled && bannerAdId.isNotEmpty()
    fun shouldShowInterstitial(): Boolean = adsEnabled && interstitialAdId.isNotEmpty()
    fun shouldShowRewarded(): Boolean = adsEnabled && rewardedAdId.isNotEmpty()
    fun shouldShowAppOpen(): Boolean = adsEnabled && appOpenAdId.isNotEmpty()
}
