package com.ofekyariv.quicktip.viewmodel

import com.ofekyariv.quicktip.data.getDefaultCurrency
import com.ofekyariv.quicktip.data.models.CurrencyInfo
import com.ofekyariv.quicktip.data.models.RoundingMode
import com.ofekyariv.quicktip.data.models.ThemeMode
import com.ofekyariv.quicktip.data.models.TipCalculation

/**
 * UI state for the tip calculator.
 * Holds all user inputs and calculated results.
 */
data class TipUiState(
    val billAmount: String = "",
    val tipPercentage: Int = 18,
    val numPeople: Int = 1,
    val selectedCurrency: CurrencyInfo = getDefaultCurrency(),
    val roundingMode: RoundingMode = RoundingMode.NO_ROUNDING,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicTheme: Boolean = false,
    val tipAmount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val perPersonAmount: Double = 0.0,
    val calculationHistory: List<TipCalculation> = emptyList(),
    val isPremium: Boolean = false,
    val showPremiumSheet: Boolean = false,
    val showSettings: Boolean = false,
    val isLoading: Boolean = false,
    val isPurchaseLoading: Boolean = false,
    val isAdLoading: Boolean = false,
    val adLoadFailed: Boolean = false,
    val error: String? = null,
    val iapError: String? = null
)
