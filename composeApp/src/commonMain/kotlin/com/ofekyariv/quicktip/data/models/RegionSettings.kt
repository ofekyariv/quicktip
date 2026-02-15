package com.ofekyariv.quicktip.data.models

/**
 * Represents regional tipping customs and defaults.
 */
data class RegionSettings(
    val name: String,                  // "North America"
    val defaultPresets: List<Int>,     // [15, 18, 20, 25]
    val defaultRounding: RoundingMode  // ROUND_UP_WHOLE
) {
    /**
     * Gets the default tip percentage (middle of the preset range).
     */
    fun getDefaultTipPercentage(): Int {
        return if (defaultPresets.isNotEmpty()) {
            defaultPresets[defaultPresets.size / 2]
        } else {
            15 // Fallback
        }
    }
}
