package com.ofekyariv.quicktip.di

import com.ofekyariv.quicktip.analytics.AnalyticsTracker
import com.ofekyariv.quicktip.viewmodel.TipViewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

/**
 * Koin dependency injection module for QuickTip app.
 */
val appModule = module {
    // Analytics
    single { AnalyticsTracker() }
    
    // ViewModels
    single { TipViewModel(get()) }
    
    // Repositories will be added in Unit 7
    // Data sources will be added in Unit 2
}

/**
 * Initialize Koin dependency injection.
 * Call this once on app startup.
 */
fun initKoin() {
    startKoin {
        modules(appModule)
    }
}
