package com.ofekyariv.quicktip.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ofekyariv.quicktip.analytics.AnalyticsTracker
import com.ofekyariv.quicktip.analytics.trackCalculationPerformed
import com.ofekyariv.quicktip.analytics.trackCalculationSaved
import com.ofekyariv.quicktip.analytics.trackCurrencyChanged
import com.ofekyariv.quicktip.analytics.trackCustomTipEntered
import com.ofekyariv.quicktip.analytics.trackErrorOccurred
import com.ofekyariv.quicktip.analytics.trackRoundingRuleChanged
import com.ofekyariv.quicktip.analytics.trackSplitChanged
import com.ofekyariv.quicktip.analytics.trackTipPresetUsed
import com.ofekyariv.quicktip.data.TipCalculator
import com.ofekyariv.quicktip.data.getCurrencyByCode
import com.ofekyariv.quicktip.data.getDefaultCurrency
import com.ofekyariv.quicktip.data.models.CurrencyInfo
import com.ofekyariv.quicktip.data.models.RoundingMode
import com.ofekyariv.quicktip.data.models.TipCalculation
import com.ofekyariv.quicktip.data.repository.CalculationRepository
import com.ofekyariv.quicktip.data.repository.SettingsRepository
import com.ofekyariv.quicktip.util.getCurrentTimeMillis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for tip calculator.
 * Manages all calculation logic and state.
 */
class TipViewModel(
    private val analytics: AnalyticsTracker,
    private val calculationRepository: CalculationRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TipUiState())
    val uiState: StateFlow<TipUiState> = _uiState.asStateFlow()
    
    init {
        // Load settings and calculation history on initialization
        viewModelScope.launch {
            // Collect settings
            settingsRepository.settings.collect { settings ->
                val isPremiumActive = settings.isPremium || getCurrentTimeMillis() < settings.rewardAdUnlockExpiry
                _uiState.update { 
                    it.copy(
                        selectedCurrency = getCurrencyByCode(settings.defaultCurrency) ?: getDefaultCurrency(),
                        tipPercentage = settings.defaultTipPercentage,
                        roundingMode = settings.defaultRoundingMode,
                        isPremium = isPremiumActive
                    ) 
                }
            }
        }
        
        viewModelScope.launch {
            // Collect calculation history
            calculationRepository.getAllCalculations().collect { calculations ->
                val currentSettings = settingsRepository.settings
                _uiState.update { 
                    it.copy(calculationHistory = calculations) 
                }
            }
        }
    }
    
    /**
     * Update bill amount and recalculate.
     */
    fun updateBillAmount(amount: String) {
        // Validate input (allow only numbers and decimal point)
        if (amount.isEmpty() || amount.matches(Regex("^\\d*\\.?\\d*$"))) {
            _uiState.update { it.copy(billAmount = amount, error = null) }
            calculateTip()
        }
    }
    
    /**
     * Update tip percentage and recalculate.
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
            _uiState.update { it.copy(error = "Tip percentage must be between 0 and 100%") }
            analytics.trackErrorOccurred("invalid_tip_percentage", "Tip percentage must be between 0 and 100%")
        }
    }
    
    /**
     * Update number of people and recalculate.
     */
    fun updateNumPeople(num: Int) {
        if (num in 1..20) {
            _uiState.update { it.copy(numPeople = num, error = null) }
            calculateTip()
            analytics.trackSplitChanged(num)
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
     * Calculate tip, total, and per-person amounts.
     * Runs automatically whenever inputs change.
     */
    private fun calculateTip() {
        val state = _uiState.value
        val billAmount = state.billAmount.toDoubleOrNull() ?: 0.0
        
        if (billAmount <= 0) {
            _uiState.update {
                it.copy(
                    tipAmount = 0.0,
                    totalAmount = 0.0,
                    perPersonAmount = 0.0
                )
            }
            return
        }
        
        // Validate bill amount
        if (billAmount < 0) {
            _uiState.update { it.copy(error = "Bill amount must be positive") }
            analytics.trackErrorOccurred("negative_bill_amount", "Bill amount must be positive")
            return
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
        
        // Calculate per-person amount
        val perPersonAmount = TipCalculator.calculateSplit(roundedTotal, state.numPeople)
        
        // Apply rounding to per-person amount
        val roundedPerPerson = TipCalculator.applyRounding(
            perPersonAmount,
            state.roundingMode,
            state.selectedCurrency.decimals
        )
        
        // Update state
        _uiState.update {
            it.copy(
                tipAmount = tipAmount,
                totalAmount = roundedTotal,
                perPersonAmount = roundedPerPerson,
                error = null
            )
        }
        
        // Track calculation performed
        analytics.trackCalculationPerformed(
            state.selectedCurrency.code,
            state.tipPercentage.toDouble(),
            state.numPeople
        )
    }
    
    /**
     * Save current calculation to history.
     * Free users limited to 10 items.
     */
    fun saveToHistory() {
        val state = _uiState.value
        val billAmount = state.billAmount.toDoubleOrNull() ?: 0.0
        
        if (billAmount <= 0) {
            _uiState.update { it.copy(error = "Please enter a bill amount first") }
            return
        }
        
        // Check free tier limit
        if (!state.isPremium && state.calculationHistory.size >= 10) {
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
            calculationRepository.saveCalculation(calculation)
            
            // Track calculation saved
            analytics.trackCalculationSaved(billAmount, state.totalAmount)
            
            _uiState.update { it.copy(error = null) }
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
                calculationHistory = it.calculationHistory,
                isPremium = it.isPremium
            )
        }
    }
    
    /**
     * Delete a calculation from history.
     */
    fun deleteCalculation(id: Long) {
        viewModelScope.launch {
            calculationRepository.deleteCalculation(id)
        }
    }
    
    /**
     * Clear all calculation history.
     */
    fun clearAllHistory() {
        viewModelScope.launch {
            calculationRepository.clearAllHistory()
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
        }
    }
    
    /**
     * Unlock premium features for 24 hours (reward ad).
     */
    fun unlockWithRewardAd() {
        viewModelScope.launch {
            settingsRepository.unlockWithRewardAd()
        }
    }
    
    /**
     * Get calculation history count.
     */
    fun getHistoryCount(): Int = _uiState.value.calculationHistory.size
    
    /**
     * Check if history limit reached.
     */
    fun isHistoryLimitReached(): Boolean {
        val state = _uiState.value
        return !state.isPremium && state.calculationHistory.size >= 10
    }
}
