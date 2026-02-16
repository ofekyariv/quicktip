package com.ofekyariv.quicktip.iap

import kotlinx.coroutines.flow.Flow

/**
 * Common interface for in-app purchases across platforms.
 * Android uses Google Play Billing, iOS uses StoreKit.
 */
expect class IAPManager {
    /**
     * Launch the purchase flow for a given product.
     * @param productId The product identifier (e.g., "premium_unlock_quicktip")
     */
    fun launchPurchaseFlow(productId: String)

    /**
     * Observe premium unlock status reactively.
     * Emits true when the user has purchased premium.
     */
    fun isPremiumUnlocked(): Flow<Boolean>

    /**
     * Restore previous purchases (e.g., after reinstall).
     */
    fun restorePurchases()
}

object IAPProducts {
    const val PREMIUM_UNLOCK = "premium_unlock_quicktip"
    const val PREMIUM_PRICE = "$0.99"
}
