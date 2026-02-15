package com.ofekyariv.quicktip.data.models

/**
 * Rounding modes for tip calculations.
 */
enum class RoundingMode {
    /** No rounding - exact amounts */
    NO_ROUNDING,
    
    /** Round up to nearest whole number (e.g., $23.47 → $24.00) */
    ROUND_UP_WHOLE,
    
    /** Round to nearest 0.50 (e.g., $23.47 → $23.50) */
    ROUND_NEAREST_HALF,
    
    /** Round to nearest 0.10 (e.g., 127.34 NIS → 127.40 NIS) */
    ROUND_NEAREST_TENTH,
    
    /** Round to nearest 5 (e.g., $23.47 → $25.00) */
    ROUND_NEAREST_FIVE;
    
    fun getDisplayName(): String = when (this) {
        NO_ROUNDING -> "No Rounding"
        ROUND_UP_WHOLE -> "Round Up to Whole"
        ROUND_NEAREST_HALF -> "Round to Nearest 0.50"
        ROUND_NEAREST_TENTH -> "Round to Nearest 0.10"
        ROUND_NEAREST_FIVE -> "Round to Nearest 5"
    }
    
    fun getExample(): String = when (this) {
        NO_ROUNDING -> "$23.47 → $23.47"
        ROUND_UP_WHOLE -> "$23.47 → $24.00"
        ROUND_NEAREST_HALF -> "$23.47 → $23.50"
        ROUND_NEAREST_TENTH -> "127.34 → 127.40"
        ROUND_NEAREST_FIVE -> "$23.47 → $25.00"
    }
}
