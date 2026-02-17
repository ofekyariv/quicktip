package com.ofekyariv.quicktip.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.ofekyariv.quicktip.ads.AdBannerView
import com.ofekyariv.quicktip.ads.AdManager
import com.ofekyariv.quicktip.data.models.RoundingMode
import com.ofekyariv.quicktip.data.models.ServiceType
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
            onSaveRoundingMode = { viewModel.saveDefaultRoundingMode(it) },
            onClearHistory = { viewModel.clearAllHistory() },
            onDismissPremiumSheet = { viewModel.showPremiumSheet(false) },
            onPurchasePremium = { viewModel.purchasePremium() },
            onRestorePurchases = { viewModel.restorePurchases() },
            onWatchAd = {
                adManager.showRewardedAd {
                    viewModel.unlockWithRewardAd()
                }
                viewModel.showPremiumSheet(false)
            },
            onRetryPurchase = { viewModel.retryPurchase() },
            onSaveCategoryTipDefault = { serviceType, percentage ->
                viewModel.saveCategoryTipDefault(serviceType, percentage)
            }
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

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Auto-save snackbar
    LaunchedEffect(uiState.autoSaveSnackbar) {
        uiState.autoSaveSnackbar?.let { msg ->
            snackbarHostState.showSnackbar(msg, duration = SnackbarDuration.Short)
            viewModel.clearAutoSaveSnackbar()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
            Spacer(modifier = Modifier.height(8.dp))

            // Service Type Selector
            ServiceTypeSelector(
                selectedServiceType = uiState.selectedServiceType,
                onServiceTypeChange = { viewModel.updateServiceType(it) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bill Amount Input Section
            BillAmountInput(
                billAmount = uiState.billAmount,
                currency = uiState.selectedCurrency,
                onBillAmountChange = { viewModel.updateBillAmount(it) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tip Percentage Presets + "+" button for custom presets
            TipPercentageSelector(
                selectedPercentage = uiState.tipPercentage,
                onPercentageChange = { viewModel.updateTipPercentage(it) },
                isPremium = uiState.isPremium,
                onCustomPresetClick = { viewModel.showPremiumSheet(true) },
                modifier = Modifier.fillMaxWidth()
            )

            // Default for [category]: X% — shown below tip buttons
            CategoryDefaultTipHint(
                serviceType = uiState.selectedServiceType,
                isPremium = uiState.isPremium,
                categoryTipDefaults = uiState.categoryTipDefaults,
                globalDefault = uiState.tipPercentage
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

            // Country Etiquette Hint Card
            uiState.currentCountryTipInfo?.let { countryInfo ->
                CountryEtiquetteCard(
                    countryTipInfo = countryInfo,
                    serviceType = uiState.selectedServiceType,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Action Button — Clear only (Save removed; auto-save is active)
            OutlinedButton(
                onClick = { viewModel.clearCalculation() },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { testTag = "clear_button" }
            ) {
                Text("Clear")
            }

            // History button
            if (uiState.calculationHistory.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onNavigateToHistory,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            contentDescription = "View History"
                            testTag = "history_button"
                        }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.List,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("History")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Shows "Default for [category]: X%" below tip buttons.
 */
@Composable
fun CategoryDefaultTipHint(
    serviceType: ServiceType,
    isPremium: Boolean,
    categoryTipDefaults: Map<ServiceType, Int>,
    globalDefault: Int
) {
    val defaultTip = if (isPremium) {
        categoryTipDefaults[serviceType] ?: globalDefault
    } else {
        globalDefault
    }
    Text(
        text = "Default for ${serviceType.label}: $defaultTip%",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp)
    )
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
    isPremium: Boolean = false,
    onCustomPresetClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showCustomDialog by remember { mutableStateOf(false) }
    var showCustomPresetExplanation by remember { mutableStateOf(false) }
    val presets = listOf(10, 15, 18, 20, 25)

    Column(modifier = modifier) {
        Text(
            text = "Tip Percentage",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Preset chips row + "+" button for custom presets
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { testTag = "tip_percentage_chips" },
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
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

            // "+" button — Set Your Own Tip % Buttons (premium feature)
            IconButton(
                onClick = {
                    if (isPremium) {
                        // TODO: Open custom tip preset editor for premium users
                        showCustomDialog = true
                    } else {
                        showCustomPresetExplanation = true
                    }
                },
                modifier = Modifier.semantics {
                    testTag = "custom_preset_add_button"
                    contentDescription = "Set Your Own Tip % Buttons"
                }
            ) {
                if (isPremium) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add custom tip preset"
                    )
                } else {
                    // 🔒 icon for non-premium
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Set Your Own Tip % Buttons — Pro feature",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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
                .heightIn(min = 48.dp)
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

    // "Set Your Own Tip % Buttons" explanation dialog for non-premium
    if (showCustomPresetExplanation) {
        AlertDialog(
            onDismissRequest = { showCustomPresetExplanation = false },
            title = { Text("Set Your Own Tip % Buttons") },
            text = {
                Text("Create your own tip buttons — Pro feature\n\nReplace 15/18/20% with your preferred percentages.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCustomPresetExplanation = false
                        onCustomPresetClick()
                    }
                ) {
                    Text("Unlock Pro")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomPresetExplanation = false }) {
                    Text("Not Now")
                }
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

            ResultRow(label = "Subtotal", amount = billAmount, currency = currency, testTag = "result_subtotal")
            Spacer(modifier = Modifier.height(8.dp))
            ResultRow(label = "Tip", amount = tipAmount, currency = currency, testTag = "result_tip", highlighted = true)
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))
            ResultRow(label = "Total", amount = totalAmount, currency = currency, testTag = "result_total", isTotal = true)

            if (numPeople > 1) {
                Spacer(modifier = Modifier.height(12.dp))
                ResultRow(label = "Per Person", amount = perPersonAmount, currency = currency, testTag = "result_per_person", highlighted = true)
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

@Composable
fun ServiceTypeSelector(
    selectedServiceType: ServiceType,
    onServiceTypeChange: (ServiceType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Service Type",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { testTag = "service_type_row_1" },
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ServiceChip(ServiceType.RESTAURANT, selectedServiceType, onServiceTypeChange, Modifier.weight(1f))
            ServiceChip(ServiceType.TAXI, selectedServiceType, onServiceTypeChange, Modifier.weight(1f))
            ServiceChip(ServiceType.SALON, selectedServiceType, onServiceTypeChange, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { testTag = "service_type_row_2" },
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ServiceChip(ServiceType.HOTEL, selectedServiceType, onServiceTypeChange, Modifier.weight(1f))
            ServiceChip(ServiceType.DELIVERY, selectedServiceType, onServiceTypeChange, Modifier.weight(1f))
            ServiceChip(ServiceType.COUNTER, selectedServiceType, onServiceTypeChange, Modifier.weight(1f))
        }
    }
}

@Composable
fun ServiceChip(
    serviceType: ServiceType,
    selectedServiceType: ServiceType,
    onServiceTypeChange: (ServiceType) -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = selectedServiceType == serviceType
    FilterChip(
        selected = isSelected,
        onClick = { onServiceTypeChange(serviceType) },
        label = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(serviceType.emoji)
                Text(serviceType.label, style = MaterialTheme.typography.bodySmall)
            }
        },
        border = if (isSelected) {
            FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = true,
                selectedBorderColor = MaterialTheme.colorScheme.primary,
                selectedBorderWidth = 2.dp
            )
        } else {
            FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = false
            )
        },
        modifier = modifier
            .heightIn(min = 48.dp)
            .semantics { testTag = "service_chip_${serviceType.name.lowercase()}" }
    )
}

@Composable
fun CountryEtiquetteCard(
    countryTipInfo: com.ofekyariv.quicktip.data.models.CountryTipInfo,
    serviceType: ServiceType,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.semantics { testTag = "country_etiquette_card" },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = countryTipInfo.flag, style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = countryTipInfo.countryName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Tipping:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                )
                Text(
                    text = countryTipInfo.culture.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyMedium,
                    color = when (countryTipInfo.culture) {
                        com.ofekyariv.quicktip.data.models.TippingCulture.EXPECTED -> MaterialTheme.colorScheme.primary
                        com.ofekyariv.quicktip.data.models.TippingCulture.APPRECIATED -> MaterialTheme.colorScheme.tertiary
                        com.ofekyariv.quicktip.data.models.TippingCulture.OPTIONAL -> MaterialTheme.colorScheme.secondary
                        com.ofekyariv.quicktip.data.models.TippingCulture.UNCOMMON -> MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                        com.ofekyariv.quicktip.data.models.TippingCulture.RUDE -> MaterialTheme.colorScheme.error
                    },
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            val tipRange = countryTipInfo.formatTipRange(serviceType)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${serviceType.emoji} ${serviceType.label}:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                )
                Text(
                    text = tipRange,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }

            if (countryTipInfo.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = countryTipInfo.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

private fun formatAmount(amount: Double): String {
    val rounded = (amount * 100).toLong() / 100.0
    val wholePart = rounded.toLong()
    val decimalPart = ((rounded - wholePart) * 100).toInt()
    return "$wholePart.${decimalPart.toString().padStart(2, '0')}"
}
