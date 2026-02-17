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
    val historyLimit: Int = 10,
    /** Per-category default tip percentages (premium feature). */
    val categoryTipDefaults: Map<ServiceType, Int> = defaultCategoryTips()
)

/**
 * Default tip percentages per service category.
 */
fun defaultCategoryTips(): Map<ServiceType, Int> = mapOf(
    ServiceType.RESTAURANT to 20,
    ServiceType.TAXI to 15,
    ServiceType.SALON to 20,
    ServiceType.HOTEL to 15,
    ServiceType.DELIVERY to 18,
    ServiceType.COUNTER to 15
)
