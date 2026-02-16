package com.ofekyariv.quicktip.di

import com.ofekyariv.quicktip.ads.AdManager
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Android-specific Koin module.
 * Provides Android platform implementations.
 */
actual fun platformModule(): Module = module {
    single { AdManager(get()) }
}
