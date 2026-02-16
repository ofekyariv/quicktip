package com.ofekyariv.quicktip.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "quicktip_settings")

private lateinit var appContext: Context

/**
 * Initialize DataStore with application context.
 * Should be called from MainActivity.onCreate().
 */
fun initializeDataStore(context: Context) {
    appContext = context.applicationContext
}

/**
 * Android implementation of DataStore factory.
 */
actual fun createDataStore(): DataStore<Preferences> {
    return appContext.dataStore
}
