import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.firebaseCrashlytics)
}

kotlin {
    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.play.services.ads)
            implementation(libs.play.billing)
            implementation(libs.sqldelight.android.driver)
            implementation(libs.androidx.datastore.preferences)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.native.driver)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)

            implementation(libs.navigation.compose)
            implementation(libs.lifecycle.runtime.compose)
            implementation(libs.material.icons.core)
            implementation(libs.material.icons.extended)
            implementation(libs.kotlinx.datetime)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)
            
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)
            implementation(libs.androidx.datastore.core)
            implementation(libs.androidx.datastore.preferences)

            implementation(libs.firebase.app)
            implementation(libs.firebase.analytics)
            implementation(libs.firebase.config)
            implementation(libs.firebase.crashlytics)
        }
    }
}

// Load local.properties for real ad IDs (not committed to git)
val localProperties = Properties().also { props ->
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { props.load(it) }
}

android {
    namespace = "com.ofekyariv.quicktip"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ofekyariv.quicktip"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        // Debug builds always use Google test ad IDs
        buildConfigField("String", "ADMOB_APP_ID", "\"ca-app-pub-3940256099942544~3347511713\"")
        buildConfigField("String", "BANNER_AD_UNIT_ID", "\"ca-app-pub-3940256099942544/6300978111\"")
        buildConfigField("String", "INTERSTITIAL_AD_UNIT_ID", "\"ca-app-pub-3940256099942544/1033173712\"")
        buildConfigField("String", "REWARDED_AD_UNIT_ID", "\"ca-app-pub-3940256099942544/5224354917\"")
        buildConfigField("String", "APP_OPEN_AD_UNIT_ID", "\"ca-app-pub-3940256099942544/9257395921\"")

        manifestPlaceholders["admobAppId"] = "ca-app-pub-3940256099942544~3347511713"
    }
    signingConfigs {
        create("release") {
            val keystorePath = localProperties.getProperty("RELEASE_KEYSTORE_PATH", "")
            if (keystorePath.isNotEmpty()) {
                storeFile = file(keystorePath)
                storePassword = localProperties.getProperty("RELEASE_KEYSTORE_PASSWORD", "")
                keyAlias = localProperties.getProperty("RELEASE_KEY_ALIAS", "")
                keyPassword = localProperties.getProperty("RELEASE_KEY_PASSWORD", "")
            }
        }
    }
    buildFeatures {
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            val releaseSigningConfig = signingConfigs.findByName("release")
            if (releaseSigningConfig?.storeFile != null) {
                signingConfig = releaseSigningConfig
            }

            // Use real ad IDs from local.properties or CI secrets for release builds
            val releaseAdmobAppId = localProperties.getProperty("ADMOB_APP_ID", "ca-app-pub-3940256099942544~3347511713")
            val releaseBannerId = localProperties.getProperty("BANNER_AD_UNIT_ID", "ca-app-pub-3940256099942544/6300978111")
            val releaseInterstitialId = localProperties.getProperty("INTERSTITIAL_AD_UNIT_ID", "ca-app-pub-3940256099942544/1033173712")
            val releaseRewardedId = localProperties.getProperty("REWARDED_AD_UNIT_ID", "ca-app-pub-3940256099942544/5224354917")
            val releaseAppOpenId = localProperties.getProperty("APP_OPEN_AD_UNIT_ID", "ca-app-pub-3940256099942544/9257395921")

            buildConfigField("String", "ADMOB_APP_ID", "\"$releaseAdmobAppId\"")
            buildConfigField("String", "BANNER_AD_UNIT_ID", "\"$releaseBannerId\"")
            buildConfigField("String", "INTERSTITIAL_AD_UNIT_ID", "\"$releaseInterstitialId\"")
            buildConfigField("String", "REWARDED_AD_UNIT_ID", "\"$releaseRewardedId\"")
            buildConfigField("String", "APP_OPEN_AD_UNIT_ID", "\"$releaseAppOpenId\"")

            manifestPlaceholders["admobAppId"] = releaseAdmobAppId
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    debugImplementation(libs.androidx.compose.ui.tooling)
}

sqldelight {
    databases {
        create("QuickTipDatabase") {
            packageName.set("com.ofekyariv.quicktip.data.database")
        }
    }
}
