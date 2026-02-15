package com.ofekyariv.quicktip.data

import com.ofekyariv.quicktip.data.models.CurrencyInfo
import com.ofekyariv.quicktip.data.models.RegionSettings
import com.ofekyariv.quicktip.data.models.RoundingMode

/**
 * Supported currencies (15+ major world currencies).
 */
val SUPPORTED_CURRENCIES = listOf(
    CurrencyInfo("USD", "$", "US Dollar", 2, "North America"),
    CurrencyInfo("EUR", "€", "Euro", 2, "Europe"),
    CurrencyInfo("GBP", "£", "British Pound", 2, "Europe"),
    CurrencyInfo("JPY", "¥", "Japanese Yen", 0, "Asia-Pacific"),
    CurrencyInfo("CAD", "C$", "Canadian Dollar", 2, "North America"),
    CurrencyInfo("AUD", "A$", "Australian Dollar", 2, "Asia-Pacific"),
    CurrencyInfo("NZD", "NZ$", "New Zealand Dollar", 2, "Asia-Pacific"),
    CurrencyInfo("CHF", "Fr", "Swiss Franc", 2, "Europe"),
    CurrencyInfo("CNY", "¥", "Chinese Yuan", 2, "Asia-Pacific"),
    CurrencyInfo("INR", "₹", "Indian Rupee", 2, "Asia-Pacific"),
    CurrencyInfo("BRL", "R$", "Brazilian Real", 2, "Latin America"),
    CurrencyInfo("MXN", "Mex$", "Mexican Peso", 2, "Latin America"),
    CurrencyInfo("ZAR", "R", "South African Rand", 2, "Middle East/Africa"),
    CurrencyInfo("SGD", "S$", "Singapore Dollar", 2, "Asia-Pacific"),
    CurrencyInfo("HKD", "HK$", "Hong Kong Dollar", 2, "Asia-Pacific"),
    CurrencyInfo("ILS", "₪", "Israeli Shekel", 2, "Middle East/Africa")
)

/**
 * Regional tipping presets and defaults.
 */
val REGIONAL_PRESETS = mapOf(
    "North America" to RegionSettings(
        name = "North America",
        defaultPresets = listOf(15, 18, 20, 25),
        defaultRounding = RoundingMode.ROUND_UP_WHOLE
    ),
    "Europe" to RegionSettings(
        name = "Europe",
        defaultPresets = listOf(5, 10, 15),
        defaultRounding = RoundingMode.NO_ROUNDING
    ),
    "Asia-Pacific" to RegionSettings(
        name = "Asia-Pacific",
        defaultPresets = listOf(0, 5, 10),
        defaultRounding = RoundingMode.NO_ROUNDING
    ),
    "Latin America" to RegionSettings(
        name = "Latin America",
        defaultPresets = listOf(10, 15, 20),
        defaultRounding = RoundingMode.NO_ROUNDING
    ),
    "Middle East/Africa" to RegionSettings(
        name = "Middle East/Africa",
        defaultPresets = listOf(10, 15, 20),
        defaultRounding = RoundingMode.ROUND_NEAREST_TENTH
    )
)

/**
 * Gets a currency by code (e.g., "USD").
 * Returns null if not found.
 */
fun getCurrencyByCode(code: String): CurrencyInfo? {
    return SUPPORTED_CURRENCIES.find { it.code == code }
}

/**
 * Gets regional settings by region name.
 * Returns null if not found.
 */
fun getRegionSettings(region: String): RegionSettings? {
    return REGIONAL_PRESETS[region]
}

/**
 * Gets all unique region names.
 */
fun getAllRegions(): List<String> {
    return REGIONAL_PRESETS.keys.toList()
}

/**
 * Gets all currencies for a specific region.
 */
fun getCurrenciesByRegion(region: String): List<CurrencyInfo> {
    return SUPPORTED_CURRENCIES.filter { it.region == region }
}

/**
 * Gets the default currency (USD).
 */
fun getDefaultCurrency(): CurrencyInfo {
    return getCurrencyByCode("USD") ?: SUPPORTED_CURRENCIES.first()
}
