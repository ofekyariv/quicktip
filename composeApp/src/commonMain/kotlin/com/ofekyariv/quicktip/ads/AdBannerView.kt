package com.ofekyariv.quicktip.ads

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Common composable for displaying a banner ad.
 * Platform-specific implementations handle the actual ad rendering.
 */
@Composable
expect fun AdBannerView(
    modifier: Modifier = Modifier
)
