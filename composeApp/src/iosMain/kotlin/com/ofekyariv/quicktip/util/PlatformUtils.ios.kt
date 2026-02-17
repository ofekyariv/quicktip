package com.ofekyariv.quicktip.util

import platform.Foundation.NSBundle
import platform.Foundation.NSDate
import platform.Foundation.NSLocale
import platform.Foundation.NSURL
import platform.Foundation.currentLocale
import platform.Foundation.timeIntervalSince1970
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
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

actual fun openUrl(url: String) {
    try {
        val nsUrl = NSURL.URLWithString(url) ?: return
        UIApplication.sharedApplication.openURL(nsUrl)
    } catch (_: Exception) { }
}

actual fun shareText(text: String) {
    try {
        val activityVC = UIActivityViewController(
            activityItems = listOf(text),
            applicationActivities = null
        )
        val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootVC?.presentViewController(activityVC, animated = true, completion = null)
    } catch (_: Exception) { }
}

actual fun getDeviceLocaleCountryCode(): String {
    return try {
        val locale = NSLocale.currentLocale
        (locale.objectForKey(platform.Foundation.NSLocaleCountryCode) as? String) ?: "US"
    } catch (_: Exception) {
        "US" // Fallback to US
    }
}
