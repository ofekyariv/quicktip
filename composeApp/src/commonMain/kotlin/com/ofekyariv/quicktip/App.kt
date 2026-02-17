package com.ofekyariv.quicktip

import androidx.compose.runtime.*
import com.ofekyariv.quicktip.analytics.AnalyticsTracker
import com.ofekyariv.quicktip.analytics.trackAppOpen
import com.ofekyariv.quicktip.di.initKoin
import com.ofekyariv.quicktip.ui.HistoryScreen
import com.ofekyariv.quicktip.ui.MainScreen
import com.ofekyariv.quicktip.ui.theme.QuickTipTheme
import com.ofekyariv.quicktip.viewmodel.TipViewModel
import org.koin.compose.koinInject

@Composable
fun App() {
    val analytics: AnalyticsTracker = koinInject()
    val viewModel: TipViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsState()

    // Navigation state
    var currentScreen by remember { mutableStateOf(Screen.Main) }

    // Track app open once when app starts
    LaunchedEffect(Unit) {
        analytics.trackAppOpen()
    }

    QuickTipTheme(
        themeMode = uiState.themeMode,
        dynamicTheme = uiState.dynamicTheme
    ) {
        when (currentScreen) {
            Screen.Main -> MainScreen(
                onNavigateToHistory = { currentScreen = Screen.History }
            )
            Screen.History -> HistoryScreen(
                onBack = { currentScreen = Screen.Main },
                onPremiumClick = { viewModel.showPremiumSheet(true) }
            )
        }
    }
}

/**
 * Simple navigation screens.
 */
enum class Screen {
    Main,
    History
}

// Initialize Koin on app start
fun initializeApp(appDeclaration: org.koin.core.KoinApplication.() -> Unit = {}) {
    initKoin(appDeclaration)
}
