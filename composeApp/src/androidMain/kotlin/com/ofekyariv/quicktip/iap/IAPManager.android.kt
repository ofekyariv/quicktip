package com.ofekyariv.quicktip.iap

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetailsResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Android implementation of IAPManager using Google Play Billing Library.
 */
actual class IAPManager(private val context: Context) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val _isPremium = MutableStateFlow(false)
    private var onPurchaseComplete: (() -> Unit)? = null

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        }
    }

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .build()

    init {
        startConnection()
    }

    private fun startConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Query existing purchases on connection
                    queryExistingPurchases()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Retry connection on next operation
            }
        })
    }

    private fun queryExistingPurchases() {
        scope.launch {
            val params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
            val result = billingClient.queryPurchasesAsync(params)
            val purchases = result.purchasesList
            val hasPremium = purchases.any { purchase ->
                purchase.products.contains(IAPProducts.PREMIUM_UNLOCK) &&
                    purchase.purchaseState == Purchase.PurchaseState.PURCHASED
            }
            _isPremium.value = hasPremium
        }
    }

    actual fun launchPurchaseFlow(productId: String) {
        scope.launch {
            val productList = listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            )
            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()

            val result: ProductDetailsResult = billingClient.queryProductDetails(params)
            val productDetails = result.productDetailsList?.firstOrNull() ?: return@launch

            val flowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(
                    listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails)
                            .build()
                    )
                )
                .build()

            val activity = context as? Activity ?: return@launch
            billingClient.launchBillingFlow(activity, flowParams)
        }
    }

    actual fun isPremiumUnlocked(): Flow<Boolean> = _isPremium.asStateFlow()

    actual fun restorePurchases() {
        if (billingClient.isReady) {
            queryExistingPurchases()
        } else {
            startConnection()
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (purchase.products.contains(IAPProducts.PREMIUM_UNLOCK)) {
                _isPremium.value = true
                onPurchaseComplete?.invoke()
            }
            // Acknowledge the purchase if not already acknowledged
            if (!purchase.isAcknowledged) {
                scope.launch {
                    val ackParams = com.android.billingclient.api.AcknowledgePurchaseParams
                        .newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                    billingClient.acknowledgePurchase(ackParams)
                }
            }
        }
    }

    fun setOnPurchaseCompleteListener(listener: () -> Unit) {
        onPurchaseComplete = listener
    }
}
