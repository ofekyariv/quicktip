package com.ofekyariv.quicktip.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ofekyariv.quicktip.ads.AdBannerView
import com.ofekyariv.quicktip.ads.AdManager
import com.ofekyariv.quicktip.data.models.RoundingMode
import com.ofekyariv.quicktip.viewmodel.TipViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToHistory: () -> Unit = {}
) {
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
            onPurchase = { viewModel.purchasePremium() },
            onRestore = { viewModel.restorePurchases() },
            onWatchAd = {
                adManager.showRewardedAd {
                    viewModel.unlockWithRewardAd()
                }
                viewModel.showPremiumSheet(false)
            },
            isPurchaseLoading = uiState.isPurchaseLoading,
            iapError = uiState.iapError,
            onRetry = { viewModel.retryPurchase() }
        )
    }

    Scaffold(
        floatingActionButton = {
            // Show history FAB when there are saved calculations
            if (uiState.calculationHistory.isNotEmpty()) {
                FloatingActionButton(
                    onClick = onNavigateToHistory,
                    modifier = Modifier.semantics { 
                        contentDescription = "View History"
                        testTag = "history_fab"
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = "History"
                        )
                        Text("History")
                    }
                }
            }
        },
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
                            modifier = Modifier.semantics { 
                                contentDescription = "Unlock Premium"
                                testTag = "premium_button"
                            }
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
                        modifier = Modifier.semantics { 
                            contentDescription = "Open Settings"
                            testTag = "settings_button"
                        }
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
            // Hide banner ads for premium users; hide if ad load failed
            if (!uiState.isPremium && !uiState.adLoadFailed) {
                AdBannerView()
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Error snackbar-style message
            AnimatedVisibility(
                visible = uiState.error != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                uiState.error?.let { errorMsg ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .semantics { testTag = "error_card" },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = errorMsg,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = { viewModel.clearError() }) {
                                Text(
                                    "Dismiss",
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }

            // Show premium banner when history limit is reached
            if (!uiState.isPremium && uiState.calculationHistory.size >= TipViewModel.FREE_HISTORY_LIMIT) {
                PremiumBanner(
                    onUpgradeClick = { viewModel.showPremiumSheet(true) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bill Amount Input Section
            BillAmountInput(
                billAmount = uiState.billAmount,
                currency = uiState.selectedCurrency,
                onBillAmountChange = { viewModel.updateBillAmount(it) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tip Percentage Presets
            TipPercentageSelector(
                selectedPercentage = uiState.tipPercentage,
                onPercentageChange = { viewModel.updateTipPercentage(it) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Split Bill Section
            SplitBillControl(
                numPeople = uiState.numPeople,
                onNumPeopleChange = { viewModel.updateNumPeople(it) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Rounding Toggle
            RoundingControl(
                roundingMode = uiState.roundingMode,
                onRoundingModeChange = { viewModel.updateRoundingMode(it) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Results Display
            ResultsCard(
                billAmount = uiState.billAmount.toDoubleOrNull() ?: 0.0,
                tipAmount = uiState.tipAmount,
                totalAmount = uiState.totalAmount,
                perPersonAmount = uiState.perPersonAmount,
                numPeople = uiState.numPeople,
                currency = uiState.selectedCurrency,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.clearCalculation() },
                    modifier = Modifier
                        .weight(1f)
                        .semantics { testTag = "clear_button" }
                ) {
                    Text("Clear")
                }

                Button(
                    onClick = { viewModel.saveToHistory() },
                    modifier = Modifier
                        .weight(1f)
                        .semantics { testTag = "save_button" },
                    enabled = uiState.billAmount.toDoubleOrNull() != null && uiState.billAmount.toDoubleOrNull()!! > 0
                ) {
                    Text("Save")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun BillAmountInput(
    billAmount: String,
    currency: com.ofekyariv.quicktip.data.models.CurrencyInfo,
    onBillAmountChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    OutlinedCard(
        modifier = modifier.semantics { testTag = "bill_amount_card" }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Bill Amount",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = billAmount,
                onValueChange = onBillAmountChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { testTag = "bill_amount_input" },
                label = { Text("Enter amount") },
                leadingIcon = {
                    Text(
                        text = currency.symbol,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                singleLine = true,
                textStyle = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@Composable
fun TipPercentageSelector(
    selectedPercentage: Int,
    onPercentageChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCustomDialog by remember { mutableStateOf(false) }
    val presets = listOf(10, 15, 18, 20, 25)

    Column(modifier = modifier) {
        Text(
            text = "Tip Percentage",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Preset chips row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { testTag = "tip_percentage_chips" },
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presets.forEach { percentage ->
                FilterChip(
                    selected = selectedPercentage == percentage,
                    onClick = { onPercentageChange(percentage) },
                    label = { Text("$percentage%") },
                    modifier = Modifier
                        .weight(1f)
                        .semantics { testTag = "tip_chip_$percentage" }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Custom percentage button/chip
        FilterChip(
            selected = !presets.contains(selectedPercentage),
            onClick = { showCustomDialog = true },
            label = {
                Text(
                    if (presets.contains(selectedPercentage)) "Custom" else "Custom ($selectedPercentage%)"
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { testTag = "tip_chip_custom" }
        )
    }

    // Custom percentage dialog
    if (showCustomDialog) {
        CustomTipDialog(
            currentPercentage = selectedPercentage,
            onDismiss = { showCustomDialog = false },
            onConfirm = { newPercentage ->
                onPercentageChange(newPercentage)
                showCustomDialog = false
            }
        )
    }
}

@Composable
fun CustomTipDialog(
    currentPercentage: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var customValue by remember { mutableStateOf(currentPercentage.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Custom Tip Percentage") },
        text = {
            OutlinedTextField(
                value = customValue,
                onValueChange = { newValue ->
                    if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                        customValue = newValue
                    }
                },
                label = { Text("Percentage (0-100)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { testTag = "custom_tip_input" }
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val value = customValue.toIntOrNull() ?: currentPercentage
                    onConfirm(value.coerceIn(0, 100))
                },
                modifier = Modifier.semantics { testTag = "custom_tip_confirm" }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SplitBillControl(
    numPeople: Int,
    onNumPeopleChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier.semantics { testTag = "split_bill_card" }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Split Bill",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalButton(
                    onClick = { onNumPeopleChange(numPeople - 1) },
                    enabled = numPeople > 1,
                    modifier = Modifier.semantics { testTag = "split_decrease" }
                ) {
                    Text("−", style = MaterialTheme.typography.headlineSmall)
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.semantics { testTag = "split_count_display" }
                ) {
                    Text(
                        text = numPeople.toString(),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (numPeople == 1) "person" else "people",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                FilledTonalButton(
                    onClick = { onNumPeopleChange(numPeople + 1) },
                    enabled = numPeople < TipViewModel.MAX_PEOPLE,
                    modifier = Modifier.semantics { testTag = "split_increase" }
                ) {
                    Text("+", style = MaterialTheme.typography.headlineSmall)
                }
            }
        }
    }
}

@Composable
fun RoundingControl(
    roundingMode: RoundingMode,
    onRoundingModeChange: (RoundingMode) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier.semantics { testTag = "rounding_card" }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Round Up to Nearest Dollar",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Makes the total an even amount",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = roundingMode == RoundingMode.ROUND_UP_WHOLE,
                onCheckedChange = { enabled ->
                    onRoundingModeChange(if (enabled) RoundingMode.ROUND_UP_WHOLE else RoundingMode.NO_ROUNDING)
                },
                modifier = Modifier.semantics { testTag = "rounding_switch" }
            )
        }
    }
}

@Composable
fun ResultsCard(
    billAmount: Double,
    tipAmount: Double,
    totalAmount: Double,
    perPersonAmount: Double,
    numPeople: Int,
    currency: com.ofekyariv.quicktip.data.models.CurrencyInfo,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.semantics { testTag = "results_card" },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Results",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Subtotal
            ResultRow(
                label = "Subtotal",
                amount = billAmount,
                currency = currency,
                testTag = "result_subtotal"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tip
            ResultRow(
                label = "Tip",
                amount = tipAmount,
                currency = currency,
                testTag = "result_tip",
                highlighted = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Total
            ResultRow(
                label = "Total",
                amount = totalAmount,
                currency = currency,
                testTag = "result_total",
                isTotal = true
            )

            // Per Person (only show when split > 1)
            if (numPeople > 1) {
                Spacer(modifier = Modifier.height(12.dp))
                ResultRow(
                    label = "Per Person",
                    amount = perPersonAmount,
                    currency = currency,
                    testTag = "result_per_person",
                    highlighted = true
                )
            }
        }
    }
}

@Composable
fun ResultRow(
    label: String,
    amount: Double,
    currency: com.ofekyariv.quicktip.data.models.CurrencyInfo,
    testTag: String,
    highlighted: Boolean = false,
    isTotal: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { this.testTag = testTag },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isTotal) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge,
            color = if (highlighted) MaterialTheme.colorScheme.primary 
                   else MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = currency.format(amount),
            style = if (isTotal) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.titleMedium,
            color = if (highlighted) MaterialTheme.colorScheme.primary 
                   else MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

/**
 * Format amount to 2 decimal places (KMP-compatible).
 */
private fun formatAmount(amount: Double): String {
    val rounded = (amount * 100).toLong() / 100.0
    val wholePart = rounded.toLong()
    val decimalPart = ((rounded - wholePart) * 100).toInt()
    return "$wholePart.${decimalPart.toString().padStart(2, '0')}"
}
