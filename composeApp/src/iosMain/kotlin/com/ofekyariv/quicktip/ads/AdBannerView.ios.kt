package com.ofekyariv.quicktip.ads

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * iOS implementation of AdBannerView.
 * Currently returns an empty Box as AdMob is not implemented for iOS.
 */
@Composable
actual fun AdBannerView(modifier: Modifier) {
    // Empty implementation for iOS
    Box(modifier = modifier)
}
