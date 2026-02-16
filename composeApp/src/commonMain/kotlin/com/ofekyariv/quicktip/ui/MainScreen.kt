package com.ofekyariv.quicktip.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ofekyariv.quicktip.ads.AdBannerView
import com.ofekyariv.quicktip.ads.AdManager
import com.ofekyariv.quicktip.viewmodel.TipViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val viewModel: TipViewModel = koinInject()
    val adManager: AdManager = koinInject()
    val uiState by viewModel.uiState.collectAsState()

    // Settings screen (full screen overlay)
    if (uiState.showSettings) {
        SettingsScreen(
            uiState = uiState,
            onBack = { viewModel.showSettings(false) },
            onCurrencyChange = { viewModel.updateCurrency(it) },
            onTipPercentageChange = { viewModel.updateTipPercentage(it) },
            onRoundingModeChange = { viewModel.updateRoundingMode(it) },
            onThemeModeChange = { viewModel.updateThemeMode(it) },
            onDynamicThemeChange = { viewModel.updateDynamicTheme(it) },
            onPremiumClick = { viewModel.showPremiumSheet(true) },
            onSaveCurrency = { viewModel.saveDefaultCurrency(it) },
            onSaveTipPercentage = { viewModel.saveDefaultTipPercentage(it) },
            onSaveRoundingMode = { viewModel.saveDefaultRoundingMode(it) }
        )
        return
    }

    // Premium bottom sheet
    if (uiState.showPremiumSheet) {
        PremiumSheet(
            onDismiss = { viewModel.showPremiumSheet(false) },
            onPurchase = {
                viewModel.purchasePremium()
            },
            onRestore = {
                viewModel.restorePurchases()
            },
            onWatchAd = {
                adManager.showRewardedAd {
                    viewModel.unlockWithRewardAd()
                }
                viewModel.showPremiumSheet(false)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("QuickTip") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    if (!uiState.isPremium) {
                        IconButton(
                            onClick = { viewModel.showPremiumSheet(true) },
                            modifier = Modifier.semantics { contentDescription = "Unlock Premium" }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Premium",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    IconButton(
                        onClick = { viewModel.showSettings(true) },
                        modifier = Modifier.semantics { contentDescription = "Open Settings" }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },
        bottomBar = {
            // Hide banner ads for premium users
            if (!uiState.isPremium) {
                AdBannerView()
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Show premium banner when history limit is reached
            if (!uiState.isPremium && uiState.calculationHistory.size >= TipViewModel.FREE_HISTORY_LIMIT) {
                PremiumBanner(
                    onUpgradeClick = { viewModel.showPremiumSheet(true) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = "QuickTip",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Worldwide Smart Tip Calculator",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Core structure ready. UI will be built in Unit 3.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}
