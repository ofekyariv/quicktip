package com.ofekyariv.quicktip

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.ofekyariv.quicktip.analytics.AnalyticsTracker
import com.ofekyariv.quicktip.analytics.trackAppOpen
import com.ofekyariv.quicktip.di.initKoin
import com.ofekyariv.quicktip.ui.MainScreen
import com.ofekyariv.quicktip.ui.theme.QuickTipTheme
import org.koin.compose.koinInject

@Composable
fun App() {
    val analytics: AnalyticsTracker = koinInject()
    
    // Track app open once when app starts
    LaunchedEffect(Unit) {
        analytics.trackAppOpen()
    }
    
    QuickTipTheme {
        MainScreen()
    }
}

// Initialize Koin on app start
fun initializeApp() {
    initKoin()
}
