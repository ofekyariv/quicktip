package com.ofekyariv.quicktip

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ofekyariv.quicktip.analytics.AnalyticsTracker
import com.ofekyariv.quicktip.analytics.trackAppOpen
import com.ofekyariv.quicktip.di.initKoin
import com.ofekyariv.quicktip.ui.MainScreen
import com.ofekyariv.quicktip.ui.theme.QuickTipTheme
import com.ofekyariv.quicktip.viewmodel.TipViewModel
import org.koin.compose.koinInject

@Composable
fun App() {
    val analytics: AnalyticsTracker = koinInject()
    val viewModel: TipViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()

    // Track app open once when app starts
    LaunchedEffect(Unit) {
        analytics.trackAppOpen()
    }

    QuickTipTheme(
        themeMode = uiState.themeMode,
        dynamicTheme = uiState.dynamicTheme
    ) {
        MainScreen()
    }
}

// Initialize Koin on app start
fun initializeApp() {
    initKoin()
}
