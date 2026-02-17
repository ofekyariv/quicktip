package com.ofekyariv.quicktip.di

import com.ofekyariv.quicktip.analytics.AnalyticsTracker
import com.ofekyariv.quicktip.data.database.DatabaseDriverFactory
import com.ofekyariv.quicktip.data.database.QuickTipDatabase
import com.ofekyariv.quicktip.data.datastore.createDataStore
import com.ofekyariv.quicktip.data.repository.CalculationRepository
import com.ofekyariv.quicktip.data.repository.SettingsRepository
import com.ofekyariv.quicktip.viewmodel.TipViewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

/**
 * Koin dependency injection module for QuickTip app.
 */
val appModule = module {
    // Analytics
    single { AnalyticsTracker() }

    // Database
    single { get<DatabaseDriverFactory>().createDriver() }
    single { QuickTipDatabase(get()) }

    // DataStore
    single { createDataStore() }

    // Repositories
    single { CalculationRepository(get()) }
    single { SettingsRepository(get()) }

    // ViewModels
    single { TipViewModel(get(), get(), get(), get()) }
}

/**
 * Initialize Koin dependency injection.
 * Call this once on app startup.
 */
fun initKoin(appDeclaration: org.koin.core.KoinApplication.() -> Unit = {}) {
    startKoin {
        appDeclaration()
        modules(appModule, platformModule())
    }
}
