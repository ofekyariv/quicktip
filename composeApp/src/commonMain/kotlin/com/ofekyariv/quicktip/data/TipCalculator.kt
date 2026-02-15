package com.ofekyariv.quicktip.data

import com.ofekyariv.quicktip.data.models.CurrencyInfo
import com.ofekyariv.quicktip.data.models.RoundingMode
import com.ofekyariv.quicktip.data.models.TipCalculation
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.round

/**
 * Core utility class for tip calculations.
 */
object TipCalculator {
    
    /**
     * Calculates the tip amount based on bill amount and tip percentage.
     * 
     * @param billAmount The original bill amount
     * @param tipPercentage The tip percentage (0-100)
     * @return The tip amount
     */
    fun calculateTip(billAmount: Double, tipPercentage: Double): Double {
        return billAmount * (tipPercentage / 100.0)
    }
    
    /**
     * Calculates the total amount (bill + tip).
     * 
     * @param billAmount The original bill amount
     * @param tipAmount The calculated tip amount
     * @return The total amount
     */
    fun calculateTotal(billAmount: Double, tipAmount: Double): Double {
        return billAmount + tipAmount
    }
    
    /**
     * Calculates the per-person amount when splitting a bill.
     * 
     * @param totalAmount The total amount to split
     * @param numPeople The number of people (must be >= 1)
     * @return The amount per person
     */
    fun calculateSplit(totalAmount: Double, numPeople: Int): Double {
        if (numPeople <= 0) return totalAmount
        return totalAmount / numPeople
    }
    
    /**
     * Applies rounding based on the selected rounding mode.
     * 
     * @param amount The amount to round
     * @param roundingMode The rounding mode to apply
     * @param decimals The number of decimal places for the currency
     * @return The rounded amount
     */
    fun applyRounding(amount: Double, roundingMode: RoundingMode, decimals: Int): Double {
        return when (roundingMode) {
            RoundingMode.NO_ROUNDING -> {
                // Round to currency precision only
                roundToPrecision(amount, decimals)
            }
            RoundingMode.ROUND_UP_WHOLE -> {
                // Round up to nearest integer
                ceil(amount)
            }
            RoundingMode.ROUND_NEAREST_HALF -> {
                // Round to nearest 0.50
                (round(amount * 2.0) / 2.0)
            }
            RoundingMode.ROUND_NEAREST_TENTH -> {
                // Round to nearest 0.10
                (round(amount * 10.0) / 10.0)
            }
            RoundingMode.ROUND_NEAREST_FIVE -> {
                // Round to nearest 5
                (round(amount / 5.0) * 5.0)
            }
        }
    }
    
    /**
     * Rounds an amount to the specified number of decimal places.
     * 
     * @param amount The amount to round
     * @param decimals The number of decimal places
     * @return The rounded amount
     */
    fun roundToPrecision(amount: Double, decimals: Int): Double {
        if (decimals <= 0) return round(amount)
        val multiplier = 10.0.pow(decimals)
        return round(amount * multiplier) / multiplier
    }
    
    /**
     * Performs a complete tip calculation with all parameters.
     * 
     * @param billAmount The original bill amount
     * @param tipPercentage The tip percentage (0-100)
     * @param numPeople The number of people splitting the bill (default: 1)
     * @param currencyInfo The currency information
     * @param roundingMode The rounding mode to apply
     * @return A complete TipCalculation object
     */
    fun calculate(
        billAmount: Double,
        tipPercentage: Double,
        numPeople: Int = 1,
        currencyInfo: CurrencyInfo,
        roundingMode: RoundingMode = RoundingMode.NO_ROUNDING
    ): TipCalculation {
        // Calculate raw tip amount
        val rawTipAmount = calculateTip(billAmount, tipPercentage)
        
        // Calculate raw total
        val rawTotal = calculateTotal(billAmount, rawTipAmount)
        
        // Apply rounding to total
        val roundedTotal = applyRounding(rawTotal, roundingMode, currencyInfo.decimals)
        
        // Calculate rounded tip (total - bill)
        val roundedTip = roundedTotal - billAmount
        
        // Calculate per-person amount
        val perPersonAmount = calculateSplit(roundedTotal, numPeople)
        
        // Round per-person to currency precision
        val roundedPerPerson = roundToPrecision(perPersonAmount, currencyInfo.decimals)
        
        return TipCalculation(
            billAmount = billAmount,
            tipPercentage = tipPercentage,
            tipAmount = roundedTip,
            totalAmount = roundedTotal,
            numPeople = numPeople,
            perPersonAmount = roundedPerPerson,
            currency = currencyInfo.code,
            roundingMode = roundingMode
        )
    }
}
