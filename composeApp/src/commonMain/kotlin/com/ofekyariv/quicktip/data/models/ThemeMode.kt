package com.ofekyariv.quicktip.data.models

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM;

    fun getDisplayName(): String = when (this) {
        LIGHT -> "Light"
        DARK -> "Dark"
        SYSTEM -> "System (Auto)"
    }
}
