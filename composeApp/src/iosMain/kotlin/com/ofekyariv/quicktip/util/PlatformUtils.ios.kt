package com.ofekyariv.quicktip.util

import platform.Foundation.NSBundle
import platform.Foundation.NSDate
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.timeIntervalSince1970
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle

actual fun getCurrentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()

actual fun performHapticFeedback() {
    try {
        val generator = UIImpactFeedbackGenerator(style = UIImpactFeedbackStyle.UIImpactFeedbackStyleLight)
        generator.prepare()
        generator.impactOccurred()
    } catch (_: Exception) {
        // Silently ignore haptic failures
    }
}

actual fun getAppVersion(): String {
    return NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String ?: "1.0.0"
}

actual fun getDeviceLocaleCountryCode(): String {
    return try {
        val locale = NSLocale.currentLocale
        locale.countryCode ?: "US"
    } catch (_: Exception) {
        "US" // Fallback to US
    }
}

actual fun getDeviceLocaleCountryCode(): String {
    return try {
        NSLocale.currentLocale.countryCode ?: "US"
    } catch (_: Exception) {
        "US" // Fallback to US
    }
}
