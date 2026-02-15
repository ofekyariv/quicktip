package com.ofekyariv.quicktip

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController(
    configure = {
        // Initialize app dependencies
        initializeApp()
    }
) {
    App()
}
