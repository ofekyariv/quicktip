package com.ofekyariv.quicktip.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import org.koin.compose.koinInject

/**
 * Android implementation of AdBannerView using Google Mobile Ads SDK.
 * Displays a banner ad using AdMob.
 */
@Composable
actual fun AdBannerView(modifier: Modifier) {
    val adManager: AdManager = koinInject()

    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = adManager.getBannerAdUnitId()
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}
