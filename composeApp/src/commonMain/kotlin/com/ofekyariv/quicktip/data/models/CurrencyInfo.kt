package com.ofekyariv.quicktip.data.models

import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Represents a currency with its properties.
 */
data class CurrencyInfo(
    val code: String,        // "USD"
    val symbol: String,      // "$"
    val name: String,        // "US Dollar"
    val decimals: Int,       // 2 (JPY = 0)
    val region: String       // "North America"
) {
    /**
     * Formats an amount with the currency symbol and proper decimal places.
     */
    fun format(amount: Double): String {
        val formatted = if (decimals == 0) {
            amount.roundToInt().toString()
        } else {
            // Round to specified decimals
            val multiplier = 10.0.pow(decimals)
            val rounded = (amount * multiplier).roundToInt() / multiplier
            // Format with decimals
            val parts = rounded.toString().split('.')
            val intPart = parts[0]
            val decPart = if (parts.size > 1) parts[1].take(decimals).padEnd(decimals, '0') else "0".repeat(decimals)
            "$intPart.$decPart"
        }
        return "$symbol$formatted"
    }
    
    /**
     * Rounds an amount to the currency's decimal precision.
     */
    fun round(amount: Double): Double {
        if (decimals == 0) {
            return amount.roundToInt().toDouble()
        }
        val multiplier = 10.0.pow(decimals)
        return (amount * multiplier).roundToInt() / multiplier
    }
}
