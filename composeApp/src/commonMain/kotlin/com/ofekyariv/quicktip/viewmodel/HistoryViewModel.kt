package com.ofekyariv.quicktip.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ofekyariv.quicktip.analytics.AnalyticsTracker
import com.ofekyariv.quicktip.analytics.trackHistoryCleared
import com.ofekyariv.quicktip.analytics.trackHistoryItemDeleted
import com.ofekyariv.quicktip.analytics.trackHistoryViewed
import com.ofekyariv.quicktip.data.getCurrencyByCode
import com.ofekyariv.quicktip.data.getDefaultCurrency
import com.ofekyariv.quicktip.data.models.CurrencyInfo
import com.ofekyariv.quicktip.data.models.TipCalculation
import com.ofekyariv.quicktip.data.repository.CalculationRepository
import com.ofekyariv.quicktip.data.repository.SettingsRepository
import com.ofekyariv.quicktip.util.getCurrentTimeMillis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the History screen.
 * Manages calculation history with premium gating.
 */
class HistoryViewModel(
    private val calculationRepository: CalculationRepository,
    private val settingsRepository: SettingsRepository,
    private val analytics: AnalyticsTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
        trackHistoryView()
    }

    /**
     * Load calculation history with premium filtering.
     */
    private fun loadHistory() {
        viewModelScope.launch {
            combine(
                calculationRepository.getAllCalculations(),
                settingsRepository.settings
            ) { allCalculations, settings ->
                val isPremiumActive = settings.isPremium || 
                    getCurrentTimeMillis() < settings.rewardAdUnlockExpiry

                // Free tier: limit to 5 most recent
                val visibleCalculations = if (isPremiumActive) {
                    allCalculations
                } else {
                    allCalculations.take(FREE_HISTORY_LIMIT)
                }

                HistoryData(
                    calculations = visibleCalculations,
                    isPremium = isPremiumActive,
                    isLimitReached = !isPremiumActive && allCalculations.size >= FREE_HISTORY_LIMIT
                )
            }.collect { data ->
                _uiState.update {
                    it.copy(
                        calculations = data.calculations,
                        isPremium = data.isPremium,
                        isLimitReached = data.isLimitReached,
                        isLoading = false,
                        error = null
                    )
                }
            }
        }
    }

    /**
     * Track history view event.
     */
    private fun trackHistoryView() {
        try {
            analytics.trackHistoryViewed(_uiState.value.calculations.size)
        } catch (_: Exception) {
            // Analytics failure - ignore silently
        }
    }

    /**
     * Delete a calculation by ID.
     */
    fun deleteCalculation(id: Long) {
        viewModelScope.launch {
            try {
                calculationRepository.deleteCalculation(id)
                analytics.trackHistoryItemDeleted()
                _uiState.update { it.copy(error = null) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to delete calculation. Please try again.")
                }
            }
        }
    }

    /**
     * Clear all calculation history (with confirmation).
     */
    fun clearAllHistory() {
        viewModelScope.launch {
            try {
                calculationRepository.clearAllHistory()
                analytics.trackHistoryCleared()
                _uiState.update { it.copy(error = null, showClearConfirmDialog = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Failed to clear history. Please try again.",
                        showClearConfirmDialog = false
                    )
                }
            }
        }
    }

    /**
     * Show or hide the clear confirmation dialog.
     */
    fun showClearConfirmDialog(show: Boolean) {
        _uiState.update { it.copy(showClearConfirmDialog = show) }
    }

    /**
     * Show or hide the premium sheet.
     */
    fun showPremiumSheet(show: Boolean) {
        _uiState.update { it.copy(showPremiumSheet = show) }
    }

    /**
     * Get currency info for a calculation.
     */
    fun getCurrencyInfo(currencyCode: String): CurrencyInfo {
        return getCurrencyByCode(currencyCode) ?: getDefaultCurrency()
    }

    /**
     * Clear error message.
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    companion object {
        const val FREE_HISTORY_LIMIT = 5
    }
}

/**
 * UI state for the History screen.
 */
data class HistoryUiState(
    val calculations: List<TipCalculation> = emptyList(),
    val isPremium: Boolean = false,
    val isLimitReached: Boolean = false,
    val isLoading: Boolean = true,
    val showClearConfirmDialog: Boolean = false,
    val showPremiumSheet: Boolean = false,
    val error: String? = null
)

/**
 * Internal data holder for combining flows.
 */
private data class HistoryData(
    val calculations: List<TipCalculation>,
    val isPremium: Boolean,
    val isLimitReached: Boolean
)
