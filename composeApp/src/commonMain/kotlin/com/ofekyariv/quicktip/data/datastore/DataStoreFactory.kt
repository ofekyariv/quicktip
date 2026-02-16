package com.ofekyariv.quicktip.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

/**
 * Platform-specific DataStore factory.
 * Implemented separately for Android and iOS.
 */
expect fun createDataStore(): DataStore<Preferences>
