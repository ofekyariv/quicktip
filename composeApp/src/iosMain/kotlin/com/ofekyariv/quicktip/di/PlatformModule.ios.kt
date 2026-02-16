package com.ofekyariv.quicktip.di

import com.ofekyariv.quicktip.ads.AdManager
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * iOS-specific Koin module.
 * Provides iOS platform implementations.
 */
actual fun platformModule(): Module = module {
    single { AdManager() }
}
