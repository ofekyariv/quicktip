package com.ofekyariv.quicktip.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ofekyariv.quicktip.data.models.RoundingMode
import com.ofekyariv.quicktip.data.models.Settings
import com.ofekyariv.quicktip.util.getCurrentTimeMillis
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for managing user settings/preferences.
 */
class SettingsRepository(private val dataStore: DataStore<Preferences>) {
    
    companion object {
        private val DEFAULT_CURRENCY = stringPreferencesKey("default_currency")
        private val DEFAULT_TIP_PERCENTAGE = intPreferencesKey("default_tip_percentage")
        private val DEFAULT_ROUNDING_MODE = stringPreferencesKey("default_rounding_mode")
        private val IS_PREMIUM = booleanPreferencesKey("is_premium")
        private val REWARD_AD_UNLOCK_EXPIRY = longPreferencesKey("reward_ad_unlock_expiry")
        private val HISTORY_LIMIT = intPreferencesKey("history_limit")
    }
    
    /**
     * Get settings as a Flow (reactive).
     */
    val settings: Flow<Settings> = dataStore.data.map { preferences ->
        Settings(
            defaultCurrency = preferences[DEFAULT_CURRENCY] ?: "USD",
            defaultTipPercentage = preferences[DEFAULT_TIP_PERCENTAGE] ?: 18,
            defaultRoundingMode = try {
                RoundingMode.valueOf(preferences[DEFAULT_ROUNDING_MODE] ?: "NO_ROUNDING")
            } catch (e: Exception) {
                RoundingMode.NO_ROUNDING
            },
            isPremium = preferences[IS_PREMIUM] ?: false,
            rewardAdUnlockExpiry = preferences[REWARD_AD_UNLOCK_EXPIRY] ?: 0L,
            historyLimit = preferences[HISTORY_LIMIT] ?: 10
        )
    }
    
    /**
     * Update default currency.
     */
    suspend fun updateDefaultCurrency(currency: String) {
        dataStore.edit { preferences ->
            preferences[DEFAULT_CURRENCY] = currency
        }
    }
    
    /**
     * Update default tip percentage.
     */
    suspend fun updateDefaultTipPercentage(percentage: Int) {
        dataStore.edit { preferences ->
            preferences[DEFAULT_TIP_PERCENTAGE] = percentage
        }
    }
    
    /**
     * Update default rounding mode.
     */
    suspend fun updateDefaultRoundingMode(mode: RoundingMode) {
        dataStore.edit { preferences ->
            preferences[DEFAULT_ROUNDING_MODE] = mode.name
        }
    }
    
    /**
     * Set premium status (after IAP purchase).
     */
    suspend fun setPremium(isPremium: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_PREMIUM] = isPremium
        }
    }
    
    /**
     * Unlock premium features for 24 hours (reward ad).
     */
    suspend fun unlockWithRewardAd() {
        val expiryTime = getCurrentTimeMillis() + (24 * 60 * 60 * 1000) // 24 hours
        dataStore.edit { preferences ->
            preferences[REWARD_AD_UNLOCK_EXPIRY] = expiryTime
        }
    }
    
    /**
     * Update history limit (for free tier).
     */
    suspend fun updateHistoryLimit(limit: Int) {
        dataStore.edit { preferences ->
            preferences[HISTORY_LIMIT] = limit
        }
    }
    
    /**
     * Clear all settings (reset to defaults).
     */
    suspend fun clearAllSettings() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
