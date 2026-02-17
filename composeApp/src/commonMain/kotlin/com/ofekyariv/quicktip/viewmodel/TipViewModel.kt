package com.ofekyariv.quicktip.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ofekyariv.quicktip.analytics.AnalyticsTracker
import com.ofekyariv.quicktip.analytics.trackCalculationPerformed
import com.ofekyariv.quicktip.analytics.trackCalculationSaved
import com.ofekyariv.quicktip.analytics.trackCurrencyChanged
import com.ofekyariv.quicktip.analytics.trackCustomTipEntered
import com.ofekyariv.quicktip.analytics.trackErrorOccurred
import com.ofekyariv.quicktip.analytics.trackPremiumPurchased
import com.ofekyariv.quicktip.analytics.trackPremiumRestored
import com.ofekyariv.quicktip.analytics.trackPremiumViewed
import com.ofekyariv.quicktip.analytics.trackRewardedUnlockActivated
import com.ofekyariv.quicktip.analytics.trackRoundingRuleChanged
import com.ofekyariv.quicktip.analytics.trackSettingsOpened
import com.ofekyariv.quicktip.analytics.trackSplitChanged
import com.ofekyariv.quicktip.analytics.trackThemeChanged
import com.ofekyariv.quicktip.analytics.trackTipPresetUsed
import com.ofekyariv.quicktip.data.TipCalculator
import com.ofekyariv.quicktip.data.getCurrencyByCode
import com.ofekyariv.quicktip.data.getDefaultCurrency
import com.ofekyariv.quicktip.data.models.CurrencyInfo
import com.ofekyariv.quicktip.data.models.RoundingMode
import com.ofekyariv.quicktip.data.models.ServiceType
import com.ofekyariv.quicktip.data.models.ThemeMode
import com.ofekyariv.quicktip.data.models.TipCalculation
import com.ofekyariv.quicktip.data.repository.CalculationRepository
import com.ofekyariv.quicktip.data.repository.SettingsRepository
import com.ofekyariv.quicktip.data.tipping.TippingDatabase
import com.ofekyariv.quicktip.ads.AdManager
import com.ofekyariv.quicktip.iap.IAPManager
import com.ofekyariv.quicktip.iap.IAPProducts
import com.ofekyariv.quicktip.util.getCurrentTimeMillis
import com.ofekyariv.quicktip.util.getDeviceLocaleCountryCode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for tip calculator.
 * Manages all calculation logic, state, validation, and error handling.
 */
class TipViewModel(
    private val analytics: AnalyticsTracker,
    private val calculationRepository: CalculationRepository,
    private val settingsRepository: SettingsRepository,
    private val iapManager: IAPManager,
    private val adManager: AdManager
) : ViewModel() {

    private var calculationsSinceLastAd = 0

    private val _uiState = MutableStateFlow(TipUiState())
    val uiState: StateFlow<TipUiState> = _uiState.asStateFlow()

    init {
        // Load country tip info based on device locale
        val deviceCountryCode = try {
            getDeviceLocaleCountryCode()
        } catch (e: Exception) {
            "US" // Fallback to US
        }
        val countryTipInfo = TippingDatabase.getByLocale(deviceCountryCode)
        
        _uiState.update {
            it.copy(currentCountryTipInfo = countryTipInfo)
        }
        
        // Load settings and calculation history on initialization
        viewModelScope.launch {
            try {
                settingsRepository.settings.collect { settings ->
                    val isPremiumActive = settings.isPremium || getCurrentTimeMillis() < settings.rewardAdUnlockExpiry
                    _uiState.update {
                        it.copy(
                            selectedCurrency = getCurrencyByCode(settings.defaultCurrency) ?: getDefaultCurrency(),
                            tipPercentage = settings.defaultTipPercentage,
                            roundingMode = settings.defaultRoundingMode,
                            themeMode = settings.themeMode,
                            dynamicTheme = settings.dynamicTheme,
                            isPremium = isPremiumActive
                        )
                    }
                }
            } catch (e: Exception) {
                // Settings load failure — use defaults silently
                logError("settings_load_failed", e.message ?: "Unknown error")
            }
        }

        viewModelScope.launch {
            try {
                calculationRepository.getAllCalculations().collect { calculations ->
                    _uiState.update {
                        it.copy(calculationHistory = calculations)
                    }
                }
            } catch (e: Exception) {
                // History load failure — use empty list
                logError("history_load_failed", e.message ?: "Unknown error")
            }
        }

        // Observe IAP premium status and persist to DataStore
        viewModelScope.launch {
            try {
                iapManager.isPremiumUnlocked().collect { isPurchased ->
                    if (isPurchased) {
                        settingsRepository.setPremium(true)
                    }
                }
            } catch (e: Exception) {
                // IAP observation failure — log silently
                logError("iap_observe_failed", e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Update bill amount and recalculate.
     * Validates: numeric only, max $999,999.99
     */
    fun updateBillAmount(amount: String) {
        // Allow empty
        if (amount.isEmpty()) {
            _uiState.update { it.copy(billAmount = "", error = null, tipAmount = 0.0, totalAmount = 0.0, perPersonAmount = 0.0) }
            return
        }

        // Validate input (allow only numbers and decimal point)
        if (!amount.matches(Regex("^\\d*\\.?\\d*$"))) {
            return
        }

        val parsed = amount.toDoubleOrNull()
        if (parsed != null && parsed > MAX_BILL_AMOUNT) {
            _uiState.update { it.copy(error = "Bill amount is too large (max $MAX_BILL_AMOUNT)") }
            analytics.trackErrorOccurred("bill_too_large", "Bill amount exceeds maximum")
            return
        }

        if (parsed != null && parsed < 0) {
            _uiState.update { it.copy(error = "Bill amount must be positive") }
            analytics.trackErrorOccurred("negative_bill_amount", "Bill amount must be positive")
            return
        }

        _uiState.update { it.copy(billAmount = amount, error = null) }
        calculateTip()
    }

    /**
     * Update tip percentage and recalculate.
     * Validates: 0-100% range.
     */
    fun updateTipPercentage(percentage: Int) {
        if (percentage in 0..100) {
            _uiState.update { it.copy(tipPercentage = percentage, error = null) }
            calculateTip()

            // Track tip preset usage
            if (percentage in listOf(10, 15, 18, 20, 25)) {
                analytics.trackTipPresetUsed(percentage.toDouble())
            } else {
                analytics.trackCustomTipEntered(percentage.toDouble())
            }
        } else {
            _uiState.update { it.copy(error = "Tip percentage must be between 0% and 100%") }
            analytics.trackErrorOccurred("invalid_tip_percentage", "Tip percentage out of range: $percentage")
        }
    }

    /**
     * Update selected service type and adjust suggested tip % based on country × service type.
     */
    fun updateServiceType(serviceType: ServiceType) {
        val countryTipInfo = _uiState.value.currentCountryTipInfo
        
        _uiState.update { it.copy(selectedServiceType = serviceType) }
        
        // Auto-adjust tip percentage based on country × service type
        if (countryTipInfo != null) {
            val suggestedTip = countryTipInfo.getSuggestedTip(serviceType)
            if (suggestedTip != null && suggestedTip > 0) {
                updateTipPercentage(suggestedTip)
            }
        }
    }

    /**
     * Update number of people and recalculate.
     * Validates: 1-99 range.
     */
    fun updateNumPeople(num: Int) {
        if (num in 1..MAX_PEOPLE) {
            _uiState.update { it.copy(numPeople = num, error = null) }
            calculateTip()
            analytics.trackSplitChanged(num)
        } else if (num < 1) {
            _uiState.update { it.copy(error = "Must have at least 1 person") }
        } else {
            _uiState.update { it.copy(error = "Maximum $MAX_PEOPLE people") }
        }
    }

    /**
     * Update selected currency and recalculate.
     */
    fun updateCurrency(currency: CurrencyInfo) {
        val oldCurrency = _uiState.value.selectedCurrency.code
        _uiState.update { it.copy(selectedCurrency = currency) }
        calculateTip()

        if (oldCurrency != currency.code) {
            analytics.trackCurrencyChanged(oldCurrency, currency.code)
        }
    }

    /**
     * Update rounding mode and recalculate.
     */
    fun updateRoundingMode(mode: RoundingMode) {
        _uiState.update { it.copy(roundingMode = mode) }
        calculateTip()
        analytics.trackRoundingRuleChanged(mode.name)
    }

    /**
     * Update theme mode and persist.
     */
    fun updateThemeMode(mode: ThemeMode) {
        _uiState.update { it.copy(themeMode = mode) }
        viewModelScope.launch {
            settingsRepository.updateThemeMode(mode)
        }
        analytics.trackThemeChanged(mode.name)
    }

    /**
     * Update dynamic theme (Material You) preference and persist.
     */
    fun updateDynamicTheme(enabled: Boolean) {
        _uiState.update { it.copy(dynamicTheme = enabled) }
        viewModelScope.launch {
            settingsRepository.updateDynamicTheme(enabled)
        }
    }

    /**
     * Show or hide the settings screen.
     */
    fun showSettings(show: Boolean) {
        _uiState.update { it.copy(showSettings = show) }
        if (show) analytics.trackSettingsOpened()
    }

    /**
     * Calculate tip, total, and per-person amounts.
     * Runs automatically whenever inputs change.
     * Defends against division by zero and invalid numbers.
     */
    private fun calculateTip() {
        val state = _uiState.value
        val billAmount = state.billAmount.toDoubleOrNull() ?: 0.0

        if (billAmount <= 0.0 || billAmount.isNaN() || billAmount.isInfinite()) {
            _uiState.update {
                it.copy(
                    tipAmount = 0.0,
                    totalAmount = 0.0,
                    perPersonAmount = 0.0
                )
            }
            return
        }

        // Small amount warning
        if (billAmount < MIN_BILL_AMOUNT) {
            _uiState.update { it.copy(error = "Bill amount seems very small") }
        }

        // Calculate tip
        val tipAmount = TipCalculator.calculateTip(billAmount, state.tipPercentage.toDouble())

        // Calculate total
        val totalAmount = TipCalculator.calculateTotal(billAmount, tipAmount)

        // Apply rounding to total
        val roundedTotal = TipCalculator.applyRounding(
            totalAmount,
            state.roundingMode,
            state.selectedCurrency.decimals
        )

        // Division by zero defense
        val numPeople = state.numPeople.coerceAtLeast(1)

        // Calculate per-person amount
        val perPersonAmount = TipCalculator.calculateSplit(roundedTotal, numPeople)

        // Apply rounding to per-person amount
        val roundedPerPerson = TipCalculator.applyRounding(
            perPersonAmount,
            state.roundingMode,
            state.selectedCurrency.decimals
        )

        // Guard against NaN/Infinity in results
        val safeTip = if (tipAmount.isNaN() || tipAmount.isInfinite()) 0.0 else tipAmount
        val safeTotal = if (roundedTotal.isNaN() || roundedTotal.isInfinite()) 0.0 else roundedTotal
        val safePerPerson = if (roundedPerPerson.isNaN() || roundedPerPerson.isInfinite()) 0.0 else roundedPerPerson

        // Update state
        _uiState.update {
            it.copy(
                tipAmount = safeTip,
                totalAmount = safeTotal,
                perPersonAmount = safePerPerson,
                error = if (billAmount >= MIN_BILL_AMOUNT) null else it.error
            )
        }

        // Track calculation performed
        try {
            analytics.trackCalculationPerformed(
                state.selectedCurrency.code,
                state.tipPercentage.toDouble(),
                numPeople
            )
        } catch (_: Exception) {
            // Analytics failure — log silently, don't disrupt UX
        }
    }

    /**
     * Save current calculation to history.
     * Free users limited to 5 items.
     */
    fun saveToHistory() {
        val state = _uiState.value
        val billAmount = state.billAmount.toDoubleOrNull() ?: 0.0

        if (billAmount <= 0) {
            _uiState.update { it.copy(error = "Please enter a bill amount first") }
            return
        }

        // Check free tier limit (5 for free users)
        if (!state.isPremium && state.calculationHistory.size >= FREE_HISTORY_LIMIT) {
            _uiState.update {
                it.copy(error = "History limit reached. Upgrade to Premium for unlimited history.")
            }
            return
        }

        val calculation = TipCalculation(
            billAmount = billAmount,
            tipPercentage = state.tipPercentage.toDouble(),
            tipAmount = state.tipAmount,
            totalAmount = state.totalAmount,
            numPeople = state.numPeople,
            perPersonAmount = state.perPersonAmount,
            currency = state.selectedCurrency.code,
            roundingMode = state.roundingMode,
            timestamp = getCurrentTimeMillis()
        )

        // Persist to database
        viewModelScope.launch {
            try {
                calculationRepository.saveCalculation(calculation)
                analytics.trackCalculationSaved(billAmount, state.totalAmount)
                _uiState.update { it.copy(error = null) }

                // Show interstitial ad every 5 calculations (free tier only)
                if (!state.isPremium) {
                    calculationsSinceLastAd++
                    if (calculationsSinceLastAd >= INTERSTITIAL_AD_FREQUENCY) {
                        adManager.showInterstitialAd()
                        calculationsSinceLastAd = 0
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to save calculation. Please try again.") }
                logError("save_calculation_failed", e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Clear current calculation.
     */
    fun clearCalculation() {
        _uiState.update {
            TipUiState(
                selectedCurrency = it.selectedCurrency,
                roundingMode = it.roundingMode,
                themeMode = it.themeMode,
                dynamicTheme = it.dynamicTheme,
                calculationHistory = it.calculationHistory,
                isPremium = it.isPremium,
                showPremiumSheet = it.showPremiumSheet
            )
        }
    }

    /**
     * Delete a calculation from history.
     */
    fun deleteCalculation(id: Long) {
        viewModelScope.launch {
            try {
                calculationRepository.deleteCalculation(id)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to delete calculation") }
                logError("delete_calculation_failed", e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Clear all calculation history.
     */
    fun clearAllHistory() {
        viewModelScope.launch {
            try {
                calculationRepository.clearAllHistory()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to clear history") }
                logError("clear_history_failed", e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Update default currency preference.
     */
    fun saveDefaultCurrency(currency: String) {
        viewModelScope.launch {
            settingsRepository.updateDefaultCurrency(currency)
        }
    }

    /**
     * Update default tip percentage preference.
     */
    fun saveDefaultTipPercentage(percentage: Int) {
        viewModelScope.launch {
            settingsRepository.updateDefaultTipPercentage(percentage)
        }
    }

    /**
     * Update default rounding mode preference.
     */
    fun saveDefaultRoundingMode(mode: RoundingMode) {
        viewModelScope.launch {
            settingsRepository.updateDefaultRoundingMode(mode)
        }
    }

    /**
     * Update premium status (after IAP purchase).
     */
    fun setPremiumStatus(isPremium: Boolean) {
        viewModelScope.launch {
            settingsRepository.setPremium(isPremium)
            if (isPremium) analytics.trackPremiumPurchased()
        }
    }

    /**
     * Launch the IAP purchase flow for premium with loading state and error handling.
     */
    fun purchasePremium() {
        _uiState.update { it.copy(isPurchaseLoading = true, iapError = null) }
        analytics.trackPremiumViewed("purchase_button")
        try {
            iapManager.launchPurchaseFlow(IAPProducts.PREMIUM_UNLOCK)
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isPurchaseLoading = false,
                    iapError = "Purchase failed. Please try again."
                )
            }
            logError("iap_purchase_failed", e.message ?: "Unknown error")
        }
        // Loading state will be cleared when IAP status flow emits
        viewModelScope.launch {
            delay(IAP_TIMEOUT_MS)
            // If still loading after timeout, clear the spinner
            if (_uiState.value.isPurchaseLoading) {
                _uiState.update { it.copy(isPurchaseLoading = false) }
            }
        }
    }

    /**
     * Retry a failed IAP purchase.
     */
    fun retryPurchase() {
        _uiState.update { it.copy(iapError = null) }
        purchasePremium()
    }

    /**
     * Restore previous premium purchases with error handling.
     */
    fun restorePurchases() {
        _uiState.update { it.copy(isPurchaseLoading = true, iapError = null) }
        try {
            iapManager.restorePurchases()
            analytics.trackPremiumRestored()
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isPurchaseLoading = false,
                    iapError = "Restore failed. Please try again."
                )
            }
            logError("iap_restore_failed", e.message ?: "Unknown error")
        }
        viewModelScope.launch {
            delay(IAP_TIMEOUT_MS)
            if (_uiState.value.isPurchaseLoading) {
                _uiState.update { it.copy(isPurchaseLoading = false) }
            }
        }
    }

    /**
     * Unlock premium features for 24 hours (reward ad).
     */
    fun unlockWithRewardAd() {
        viewModelScope.launch {
            settingsRepository.unlockWithRewardAd()
            analytics.trackRewardedUnlockActivated()
        }
    }

    /**
     * Handle ad load failure — hides ad area and retries once after delay.
     */
    fun onAdLoadFailed() {
        _uiState.update { it.copy(adLoadFailed = true, isAdLoading = false) }
        logError("ad_load_failed", "Banner ad failed to load")

        // Retry once after 5 seconds
        viewModelScope.launch {
            delay(AD_RETRY_DELAY_MS)
            _uiState.update { it.copy(adLoadFailed = false, isAdLoading = true) }
        }
    }

    /**
     * Handle successful ad load.
     */
    fun onAdLoaded() {
        _uiState.update { it.copy(adLoadFailed = false, isAdLoading = false) }
    }

    /**
     * Show or hide the premium bottom sheet.
     */
    fun showPremiumSheet(show: Boolean) {
        _uiState.update { it.copy(showPremiumSheet = show, iapError = null) }
        if (show) analytics.trackPremiumViewed("sheet")
    }

    /**
     * Clear the current error message.
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Clear the IAP error message.
     */
    fun clearIapError() {
        _uiState.update { it.copy(iapError = null) }
    }

    /**
     * Get calculation history count.
     */
    fun getHistoryCount(): Int = _uiState.value.calculationHistory.size

    /**
     * Check if history limit reached (free: 5 max).
     */
    fun isHistoryLimitReached(): Boolean {
        val state = _uiState.value
        return !state.isPremium && state.calculationHistory.size >= FREE_HISTORY_LIMIT
    }

    private fun logError(type: String, message: String) {
        try {
            analytics.trackErrorOccurred(type, message)
        } catch (_: Exception) {
            // Analytics itself failed — ignore silently
        }
    }

    companion object {
        const val FREE_HISTORY_LIMIT = 5
        const val INTERSTITIAL_AD_FREQUENCY = 5
        const val MAX_BILL_AMOUNT = 99_999.99
        const val MIN_BILL_AMOUNT = 0.01
        const val MAX_PEOPLE = 20
        const val AD_RETRY_DELAY_MS = 5000L
        const val IAP_TIMEOUT_MS = 30_000L
    }
}
