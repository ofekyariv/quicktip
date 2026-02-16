package com.ofekyariv.quicktip.di

import com.ofekyariv.quicktip.ads.AdManager
import com.ofekyariv.quicktip.data.database.DatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Android-specific Koin module.
 * Provides Android platform implementations.
 */
actual fun platformModule(): Module = module {
    single { DatabaseDriverFactory(get()) }
    single { AdManager(get()) }
}
