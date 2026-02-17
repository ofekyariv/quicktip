package com.ofekyariv.quicktip.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual fun getCurrentTimeMillis(): Long = System.currentTimeMillis()

private object HapticHelper : KoinComponent {
    val context: Context by inject()
}

actual fun performHapticFeedback() {
    try {
        val context = HapticHelper.context
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator?.vibrate(
                VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        }
    } catch (_: Exception) {
        // Silently ignore haptic failures
    }
}

actual fun getAppVersion(): String {
    return try {
        val context = HapticHelper.context
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName ?: "1.0.0"
    } catch (_: Exception) {
        "1.0.0"
    }
}

actual fun getDeviceLocaleCountryCode(): String {
    return try {
        val context = HapticHelper.context
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0].country
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale.country
        }
    } catch (_: Exception) {
        "US" // Fallback to US
    }
}
