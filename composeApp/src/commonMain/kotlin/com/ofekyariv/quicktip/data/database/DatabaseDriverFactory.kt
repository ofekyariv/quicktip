package com.ofekyariv.quicktip.data.database

import app.cash.sqldelight.db.SqlDriver

/**
 * Platform-specific database driver factory.
 * Implemented separately for Android and iOS.
 */
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}
