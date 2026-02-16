package com.ofekyariv.quicktip.di

import org.koin.core.module.Module

/**
 * Platform-specific Koin module.
 * Each platform provides its own implementation.
 */
expect fun platformModule(): Module
