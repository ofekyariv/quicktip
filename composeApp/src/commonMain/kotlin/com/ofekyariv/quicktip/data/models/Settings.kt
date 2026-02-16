package com.ofekyariv.quicktip.data.models

/**
 * User preferences and settings.
 */
data class Settings(
    val defaultCurrency: String = "USD",
    val defaultTipPercentage: Int = 18,
    val defaultRoundingMode: RoundingMode = RoundingMode.NO_ROUNDING,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicTheme: Boolean = false,
    val isPremium: Boolean = false,
    val rewardAdUnlockExpiry: Long = 0L,
    val historyLimit: Int = 5
)
