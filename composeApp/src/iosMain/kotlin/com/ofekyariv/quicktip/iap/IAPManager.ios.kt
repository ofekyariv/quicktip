package com.ofekyariv.quicktip.iap

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.Foundation.NSLog
import platform.StoreKit.SKPayment
import platform.StoreKit.SKPaymentQueue
import platform.StoreKit.SKPaymentTransaction
import platform.StoreKit.SKPaymentTransactionObserverProtocol
import platform.StoreKit.SKProduct
import platform.StoreKit.SKProductsRequest
import platform.StoreKit.SKProductsRequestDelegateProtocol
import platform.StoreKit.SKProductsResponse
import platform.darwin.NSObject

// StoreKit transaction state constants (SKPaymentTransactionState values)
private const val SK_STATE_PURCHASED = 1L
private const val SK_STATE_FAILED = 2L
private const val SK_STATE_RESTORED = 3L

/**
 * iOS implementation of IAPManager using StoreKit.
 */
actual class IAPManager : NSObject(), SKPaymentTransactionObserverProtocol, SKProductsRequestDelegateProtocol {
    private val _isPremium = MutableStateFlow(false)
    private var premiumProduct: SKProduct? = null

    init {
        SKPaymentQueue.defaultQueue().addTransactionObserver(this)
        fetchProducts()
    }

    private fun fetchProducts() {
        val productIds = setOf(IAPProducts.PREMIUM_UNLOCK)
        @Suppress("UNCHECKED_CAST")
        val request = SKProductsRequest(productIdentifiers = productIds as Set<Any?>)
        request.delegate = this
        request.start()
    }

    actual fun launchPurchaseFlow(productId: String) {
        val product = premiumProduct
        if (product != null) {
            val payment = SKPayment.paymentWithProduct(product)
            SKPaymentQueue.defaultQueue().addPayment(payment)
        } else {
            NSLog("IAPManager: Product not yet loaded, fetching...")
            fetchProducts()
        }
    }

    actual fun isPremiumUnlocked(): Flow<Boolean> = _isPremium.asStateFlow()

    actual fun restorePurchases() {
        SKPaymentQueue.defaultQueue().restoreCompletedTransactions()
    }

    // SKProductsRequestDelegate
    override fun productsRequest(request: SKProductsRequest, didReceiveResponse: SKProductsResponse) {
        val products = didReceiveResponse.products
        for (product in products) {
            val skProduct = product as? SKProduct ?: continue
            if (skProduct.productIdentifier == IAPProducts.PREMIUM_UNLOCK) {
                premiumProduct = skProduct
            }
        }
    }

    // SKPaymentTransactionObserver
    override fun paymentQueue(
        queue: SKPaymentQueue,
        updatedTransactions: List<*>
    ) {
        for (transaction in updatedTransactions) {
            val skTransaction = transaction as? SKPaymentTransaction ?: continue
            @Suppress("USELESS_CAST")
            when (skTransaction.transactionState as Long) {
                SK_STATE_PURCHASED -> {
                    if (skTransaction.payment.productIdentifier == IAPProducts.PREMIUM_UNLOCK) {
                        _isPremium.value = true
                    }
                    queue.finishTransaction(skTransaction)
                }
                SK_STATE_RESTORED -> {
                    val originalProductId = skTransaction.originalTransaction?.payment?.productIdentifier
                    if (originalProductId == IAPProducts.PREMIUM_UNLOCK) {
                        _isPremium.value = true
                    }
                    queue.finishTransaction(skTransaction)
                }
                SK_STATE_FAILED -> {
                    NSLog("IAPManager: Transaction failed: ${skTransaction.error?.localizedDescription}")
                    queue.finishTransaction(skTransaction)
                }
                else -> {}
            }
        }
    }
}
