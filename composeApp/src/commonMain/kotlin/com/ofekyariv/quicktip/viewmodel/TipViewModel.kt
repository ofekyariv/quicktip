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

    /** Counter for interstitial ad trigger: show after 3rd calculation per session, cap 2/session */
    private var calculationsThisSession = 0
    private var interstitialsShownThisSession = 0

    private val _uiState = MutableStateFlow(TipUiState())
    val uiState: StateFlow<TipUiState> = _uiState.asStateFlow()

    init {
        // Fetch Remote Config and show app-open ad on cold start
        viewModelScope.launch {
            adManager.fetchRemoteConfig()
            onAppColdStart()
        }

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
                            isPremium = isPremiumActive,
                            categoryTipDefaults = settings.categoryTipDefaults
                        )
                    }
                }
            } catch (e: Exception) {
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
                logError("iap_observe_failed", e.message ?: "Unknown error")
            }
        }
    }

    // ─── Ad Scaffold (placeholders) ───────────────────────────────────────

    /**
     * Show an app-open ad on cold start (with cooldown from Remote Config).
     * Premium users skip all ad triggers.
     */
    private fun onAppColdStart() {
        if (!_uiState.value.isPremium) {
            adManager.loadAppOpenAd()
            adManager.showAppOpenAd()
        }
    }

    /**
     * Show interstitial after 3rd calculation per session (cap 2/session).
     * Checks RemoteAdConfig via AdManager. Premium users skip.
     */
    private fun maybeShowInterstitialAd() {
        val state = _uiState.value
        if (state.isPremium) return
        calculationsThisSession++
        if (calculationsThisSession >= INTERSTITIAL_CALC_THRESHOLD && interstitialsShownThisSession < MAX_INTERSTITIALS_PER_SESSION) {
            if (adManager.showInterstitialAd()) {
                interstitialsShownThisSession++
            }
            calculationsThisSession = 0
        }
    }

    /**
     * TODO: Ad scaffold — Rewarded video at history limit.
     * "Watch ad to save 5 more" — extends free limit by 5 temporarily.
     * Premium users skip.
     */
    fun showRewardedAdForExtraHistory() {
        // TODO: Implement actual AdMob rewarded video ad
        // adManager.showRewardedAd { onRewardedHistoryExtension() }
        // For now, placeholder:
        onRewardedHistoryExtension()
    }

    /**
     * Callback after rewarded ad watched — grants 5 extra history slots.
     */
    private fun onRewardedHistoryExtension() {
        // TODO: Implement temporary history extension (e.g., +5 slots for this session)
        // For now this is a no-op placeholder
    }

    /**
     * TODO: Ad scaffold — Native ad every 5th history entry.
     * Returns true if a native ad placeholder should be shown at this index.
     * Premium users skip.
     */
    fun shouldShowNativeAdAtIndex(index: Int): Boolean {
        if (_uiState.value.isPremium) return false
        // TODO: Replace with actual native ad rendering
        return (index + 1) % NATIVE_AD_FREQUENCY == 0
    }

    // ─── Bill & Calculation ──────────────────────────────────────────────

    /**
     * Update bill amount and recalculate.
     * Validates: numeric only, max $999,999.99
     */
    fun updateBillAmount(amount: String) {
        if (amount.isEmpty()) {
            _uiState.update { it.copy(billAmount = "", error = null, tipAmount = 0.0, totalAmount = 0.0, perPersonAmount = 0.0) }
            return
        }

        if (!amount.matches(Regex("^\\d*\\.?\\d*$"))) return

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
     */
    fun updateTipPercentage(percentage: Int) {
        if (percentage in 0..100) {
            _uiState.update { it.copy(tipPercentage = percentage, error = null) }
            calculateTip()

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
     * Update selected service type and adjust tip % based on per-category defaults or country data.
     */
    fun updateServiceType(serviceType: ServiceType) {
        val state = _uiState.value
        _uiState.update { it.copy(selectedServiceType = serviceType) }

        // Per-category default tip (premium) or country-based suggestion
        if (state.isPremium) {
            val categoryDefault = state.categoryTipDefaults[serviceType]
            if (categoryDefault != null) {
                updateTipPercentage(categoryDefault)
                return
            }
        }

        // Fallback: country-based suggestion
        val countryTipInfo = state.currentCountryTipInfo
        if (countryTipInfo != null) {
            val suggestedTip = countryTipInfo.getSuggestedTip(serviceType)
            if (suggestedTip != null && suggestedTip > 0) {
                updateTipPercentage(suggestedTip)
            }
        }
    }

    /**
     * Update number of people and recalculate.
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

    fun updateCurrency(currency: CurrencyInfo) {
        val oldCurrency = _uiState.value.selectedCurrency.code
        _uiState.update { it.copy(selectedCurrency = currency) }
        calculateTip()
        if (oldCurrency != currency.code) {
            analytics.trackCurrencyChanged(oldCurrency, currency.code)
        }
    }

    fun updateRoundingMode(mode: RoundingMode) {
        _uiState.update { it.copy(roundingMode = mode) }
        calculateTip()
        analytics.trackRoundingRuleChanged(mode.name)
    }

    fun updateThemeMode(mode: ThemeMode) {
        _uiState.update { it.copy(themeMode = mode) }
        viewModelScope.launch { settingsRepository.updateThemeMode(mode) }
        analytics.trackThemeChanged(mode.name)
    }

    fun updateDynamicTheme(enabled: Boolean) {
        _uiState.update { it.copy(dynamicTheme = enabled) }
        viewModelScope.launch { settingsRepository.updateDynamicTheme(enabled) }
    }

    fun showSettings(show: Boolean) {
        _uiState.update { it.copy(showSettings = show) }
        if (show) analytics.trackSettingsOpened()
    }

    /**
     * Calculate tip, total, and per-person amounts.
     * Auto-saves result to history.
     */
    private fun calculateTip() {
        val state = _uiState.value
        val billAmount = state.billAmount.toDoubleOrNull() ?: 0.0

        if (billAmount <= 0.0 || billAmount.isNaN() || billAmount.isInfinite()) {
            _uiState.update {
                it.copy(tipAmount = 0.0, totalAmount = 0.0, perPersonAmount = 0.0)
            }
            return
        }

        if (billAmount < MIN_BILL_AMOUNT) {
            _uiState.update { it.copy(error = "Bill amount seems very small") }
        }

        val tipAmount = TipCalculator.calculateTip(billAmount, state.tipPercentage.toDouble())
        val totalAmount = TipCalculator.calculateTotal(billAmount, tipAmount)
        val roundedTotal = TipCalculator.applyRounding(totalAmount, state.roundingMode, state.selectedCurrency.decimals)
        val numPeople = state.numPeople.coerceAtLeast(1)
        val perPersonAmount = TipCalculator.calculateSplit(roundedTotal, numPeople)
        val roundedPerPerson = TipCalculator.applyRounding(perPersonAmount, state.roundingMode, state.selectedCurrency.decimals)

        val safeTip = if (tipAmount.isNaN() || tipAmount.isInfinite()) 0.0 else tipAmount
        val safeTotal = if (roundedTotal.isNaN() || roundedTotal.isInfinite()) 0.0 else roundedTotal
        val safePerPerson = if (roundedPerPerson.isNaN() || roundedPerPerson.isInfinite()) 0.0 else roundedPerPerson

        _uiState.update {
            it.copy(
                tipAmount = safeTip,
                totalAmount = safeTotal,
                perPersonAmount = safePerPerson,
                error = if (billAmount >= MIN_BILL_AMOUNT) null else it.error
            )
        }

        try {
            analytics.trackCalculationPerformed(state.selectedCurrency.code, state.tipPercentage.toDouble(), numPeople)
        } catch (_: Exception) {}

        // Auto-save every calculation
        autoSave(billAmount, safeTip, safeTotal, numPeople, safePerPerson)
    }

    /**
     * Auto-save current calculation to history.
     * Free users: FIFO at 10 — oldest is overwritten, not hard-blocked.
     */
    private fun autoSave(billAmount: Double, tipAmount: Double, totalAmount: Double, numPeople: Int, perPersonAmount: Double) {
        if (billAmount <= 0) return

        val state = _uiState.value
        val calculation = TipCalculation(
            billAmount = billAmount,
            tipPercentage = state.tipPercentage.toDouble(),
            tipAmount = tipAmount,
            totalAmount = totalAmount,
            numPeople = numPeople,
            perPersonAmount = perPersonAmount,
            currency = state.selectedCurrency.code,
            roundingMode = state.roundingMode,
            timestamp = getCurrentTimeMillis()
        )

        viewModelScope.launch {
            try {
                // FIFO: if at limit, delete oldest before saving
                if (!state.isPremium && state.calculationHistory.size >= FREE_HISTORY_LIMIT) {
                    calculationRepository.keepRecentOnly(FREE_HISTORY_LIMIT - 1)
                }

                calculationRepository.saveCalculation(calculation)
                analytics.trackCalculationSaved(billAmount, totalAmount)

                // Show snackbar with count
                val currentCount = (state.calculationHistory.size + 1).coerceAtMost(FREE_HISTORY_LIMIT)
                val snackbarMsg = if (state.isPremium) {
                    "Saved ✓"
                } else {
                    "Saved ✓ ($currentCount/$FREE_HISTORY_LIMIT free)"
                }
                _uiState.update { it.copy(autoSaveSnackbar = snackbarMsg, error = null) }

                // Ad scaffold: interstitial after 3rd calculation
                maybeShowInterstitialAd()

            } catch (e: Exception) {
                logError("auto_save_failed", e.message ?: "Unknown error")
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
                showPremiumSheet = it.showPremiumSheet,
                categoryTipDefaults = it.categoryTipDefaults
            )
        }
    }

    fun clearAutoSaveSnackbar() {
        _uiState.update { it.copy(autoSaveSnackbar = null) }
    }

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

    fun saveDefaultCurrency(currency: String) {
        viewModelScope.launch { settingsRepository.updateDefaultCurrency(currency) }
    }

    fun saveDefaultTipPercentage(percentage: Int) {
        viewModelScope.launch { settingsRepository.updateDefaultTipPercentage(percentage) }
    }

    fun saveDefaultRoundingMode(mode: RoundingMode) {
        viewModelScope.launch { settingsRepository.updateDefaultRoundingMode(mode) }
    }

    /**
     * Save per-category default tip percentage (premium feature).
     */
    fun saveCategoryTipDefault(serviceType: ServiceType, percentage: Int) {
        viewModelScope.launch {
            settingsRepository.updateCategoryTipDefault(serviceType, percentage)
        }
    }

    fun setPremiumStatus(isPremium: Boolean) {
        viewModelScope.launch {
            settingsRepository.setPremium(isPremium)
            if (isPremium) analytics.trackPremiumPurchased()
        }
    }

    fun purchasePremium() {
        _uiState.update { it.copy(isPurchaseLoading = true, iapError = null) }
        analytics.trackPremiumViewed("purchase_button")
        try {
            iapManager.launchPurchaseFlow(IAPProducts.PREMIUM_UNLOCK)
        } catch (e: Exception) {
            _uiState.update { it.copy(isPurchaseLoading = false, iapError = "Purchase failed. Please try again.") }
            logError("iap_purchase_failed", e.message ?: "Unknown error")
        }
        viewModelScope.launch {
            delay(IAP_TIMEOUT_MS)
            if (_uiState.value.isPurchaseLoading) {
                _uiState.update { it.copy(isPurchaseLoading = false) }
            }
        }
    }

    fun retryPurchase() {
        _uiState.update { it.copy(iapError = null) }
        purchasePremium()
    }

    fun restorePurchases() {
        _uiState.update { it.copy(isPurchaseLoading = true, iapError = null) }
        try {
            iapManager.restorePurchases()
            analytics.trackPremiumRestored()
        } catch (e: Exception) {
            _uiState.update { it.copy(isPurchaseLoading = false, iapError = "Restore failed. Please try again.") }
            logError("iap_restore_failed", e.message ?: "Unknown error")
        }
        viewModelScope.launch {
            delay(IAP_TIMEOUT_MS)
            if (_uiState.value.isPurchaseLoading) {
                _uiState.update { it.copy(isPurchaseLoading = false) }
            }
        }
    }

    fun unlockWithRewardAd() {
        viewModelScope.launch {
            settingsRepository.unlockWithRewardAd()
            analytics.trackRewardedUnlockActivated()
        }
    }

    fun onAdLoadFailed() {
        _uiState.update { it.copy(adLoadFailed = true, isAdLoading = false) }
        logError("ad_load_failed", "Banner ad failed to load")
        viewModelScope.launch {
            delay(AD_RETRY_DELAY_MS)
            _uiState.update { it.copy(adLoadFailed = false, isAdLoading = true) }
        }
    }

    fun onAdLoaded() {
        _uiState.update { it.copy(adLoadFailed = false, isAdLoading = false) }
    }

    fun showPremiumSheet(show: Boolean) {
        _uiState.update { it.copy(showPremiumSheet = show, iapError = null) }
        if (show) analytics.trackPremiumViewed("sheet")
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearIapError() {
        _uiState.update { it.copy(iapError = null) }
    }

    fun getHistoryCount(): Int = _uiState.value.calculationHistory.size

    fun isHistoryLimitReached(): Boolean {
        val state = _uiState.value
        return !state.isPremium && state.calculationHistory.size >= FREE_HISTORY_LIMIT
    }

    /**
     * Get the default tip % for the currently selected category.
     * Returns the per-category default for premium, or global default for free.
     */
    fun getDefaultTipForCurrentCategory(): Int {
        val state = _uiState.value
        return if (state.isPremium) {
            state.categoryTipDefaults[state.selectedServiceType] ?: state.tipPercentage
        } else {
            state.tipPercentage
        }
    }

    private fun logError(type: String, message: String) {
        try {
            analytics.trackErrorOccurred(type, message)
        } catch (_: Exception) {}
    }

    companion object {
        const val FREE_HISTORY_LIMIT = 10
        const val INTERSTITIAL_CALC_THRESHOLD = 3
        const val MAX_INTERSTITIALS_PER_SESSION = 2
        const val NATIVE_AD_FREQUENCY = 5
        const val MAX_BILL_AMOUNT = 99_999.99
        const val MIN_BILL_AMOUNT = 0.01
        const val MAX_PEOPLE = 20
        const val AD_RETRY_DELAY_MS = 5000L
        const val IAP_TIMEOUT_MS = 30_000L
    }
}
