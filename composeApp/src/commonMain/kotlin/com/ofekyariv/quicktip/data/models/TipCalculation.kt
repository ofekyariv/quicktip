package com.ofekyariv.quicktip.data.models

import com.ofekyariv.quicktip.util.getCurrentTimeMillis

/**
 * Represents a complete tip calculation with all parameters and results.
 */
data class TipCalculation(
    val id: Long = 0L,
    val billAmount: Double,
    val tipPercentage: Double,
    val tipAmount: Double,
    val totalAmount: Double,
    val numPeople: Int,
    val perPersonAmount: Double,
    val currency: String,
    val roundingMode: RoundingMode,
    val timestamp: Long = getCurrentTimeMillis()
) {
    /**
     * Formats the bill amount with currency symbol.
     */
    fun formattedBillAmount(currencyInfo: CurrencyInfo): String {
        return currencyInfo.format(billAmount)
    }
    
    /**
     * Formats the tip amount with currency symbol.
     */
    fun formattedTipAmount(currencyInfo: CurrencyInfo): String {
        return currencyInfo.format(tipAmount)
    }
    
    /**
     * Formats the total amount with currency symbol.
     */
    fun formattedTotalAmount(currencyInfo: CurrencyInfo): String {
        return currencyInfo.format(totalAmount)
    }
    
    /**
     * Formats the per-person amount with currency symbol.
     */
    fun formattedPerPersonAmount(currencyInfo: CurrencyInfo): String {
        return currencyInfo.format(perPersonAmount)
    }
}
