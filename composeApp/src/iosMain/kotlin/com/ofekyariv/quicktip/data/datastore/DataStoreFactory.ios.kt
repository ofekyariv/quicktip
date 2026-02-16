package com.ofekyariv.quicktip.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

/**
 * iOS implementation of DataStore factory.
 */
@OptIn(ExperimentalForeignApi::class)
actual fun createDataStore(): DataStore<Preferences> {
    return createDataStoreWithPath(
        producePath = {
            val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null
            )
            requireNotNull(documentDirectory).path + "/quicktip_settings.preferences_pb"
        }
    )
}

private fun createDataStoreWithPath(producePath: () -> String): DataStore<Preferences> {
    return androidx.datastore.preferences.core.PreferenceDataStoreFactory.create(
        produceFile = { producePath() }
    )
}
