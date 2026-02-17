package com.ofekyariv.quicktip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import com.ofekyariv.quicktip.ads.AdManager
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Only initialize on first creation (not on rotation)
        if (savedInstanceState == null) {
            // Initialize Firebase
            Firebase.initialize(this)

            // Initialize DataStore before Koin
            com.ofekyariv.quicktip.data.datastore.initializeDataStore(this)

            // Initialize app dependencies with Android context
            initializeApp {
                androidContext(this@MainActivity.applicationContext)
            }

            // Initialize AdMob
            val adManager: AdManager by inject()
            adManager.initialize()

            // Pre-load ads
            adManager.loadInterstitialAd()
            adManager.loadRewardedAd()
        }

        enableEdgeToEdge()
        setContent {
            val isDark = isSystemInDarkTheme()
            // Ensure status bar icons are dark in light mode, light in dark mode
            LaunchedEffect(isDark) {
                enableEdgeToEdge(
                    statusBarStyle = if (isDark) {
                        SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(
                            android.graphics.Color.TRANSPARENT,
                            android.graphics.Color.TRANSPARENT
                        )
                    },
                    navigationBarStyle = if (isDark) {
                        SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(
                            android.graphics.Color.TRANSPARENT,
                            android.graphics.Color.TRANSPARENT
                        )
                    }
                )
            }
            App()
        }
    }
}
