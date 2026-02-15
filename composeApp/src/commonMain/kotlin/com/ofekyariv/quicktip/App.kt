package com.ofekyariv.quicktip

import androidx.compose.runtime.Composable
import com.ofekyariv.quicktip.di.initKoin
import com.ofekyariv.quicktip.ui.MainScreen
import com.ofekyariv.quicktip.ui.theme.QuickTipTheme

@Composable
fun App() {
    QuickTipTheme {
        MainScreen()
    }
}

// Initialize Koin on app start
fun initializeApp() {
    initKoin()
}
