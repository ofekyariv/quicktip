package com.ofekyariv.quicktip

import android.os.Bundle
import androidx.activity.ComponentActivity
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
    private val adManager: AdManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        Firebase.initialize(this)

        // Initialize DataStore before Koin
        com.ofekyariv.quicktip.data.datastore.initializeDataStore(this)

        // Initialize app dependencies with Android context
        initializeApp {
            androidContext(this@MainActivity.applicationContext)
        }

        // Initialize AdMob
        adManager.initialize()

        // Pre-load ads
        adManager.loadInterstitialAd()
        adManager.loadRewardedAd()

        enableEdgeToEdge()
        setContent {
            // Remove when https://issuetracker.google.com/issues/364713509 is fixed
            LaunchedEffect(isSystemInDarkTheme()) {
                enableEdgeToEdge()
            }
            App()
        }
    }
}
