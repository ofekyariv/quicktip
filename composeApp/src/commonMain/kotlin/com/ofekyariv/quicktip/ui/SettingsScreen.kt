package com.ofekyariv.quicktip.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ofekyariv.quicktip.data.SUPPORTED_CURRENCIES
import com.ofekyariv.quicktip.data.models.CurrencyInfo
import com.ofekyariv.quicktip.data.models.RoundingMode
import com.ofekyariv.quicktip.data.models.ThemeMode
import com.ofekyariv.quicktip.util.getAppVersion
import com.ofekyariv.quicktip.util.performHapticFeedback
import com.ofekyariv.quicktip.viewmodel.TipUiState
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: TipUiState,
    onBack: () -> Unit,
    onCurrencyChange: (CurrencyInfo) -> Unit,
    onTipPercentageChange: (Int) -> Unit,
    onRoundingModeChange: (RoundingMode) -> Unit,
    onThemeModeChange: (ThemeMode) -> Unit,
    onDynamicThemeChange: (Boolean) -> Unit,
    onPremiumClick: () -> Unit,
    onSaveCurrency: (String) -> Unit,
    onSaveTipPercentage: (Int) -> Unit,
    onSaveRoundingMode: (RoundingMode) -> Unit,
    onClearHistory: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            performHapticFeedback()
                            onBack()
                        },
                        modifier = Modifier.semantics { contentDescription = "Go back" }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Calculator Defaults section
            SettingsSectionHeader("Calculator Defaults")

            // Default Currency
            CurrencyDropdown(
                selectedCurrency = uiState.selectedCurrency,
                onCurrencySelected = { currency ->
                    performHapticFeedback()
                    onCurrencyChange(currency)
                    onSaveCurrency(currency.code)
                }
            )

            // Default Tip % slider
            TipPercentageSlider(
                tipPercentage = uiState.tipPercentage,
                onTipPercentageChange = { percentage ->
                    onTipPercentageChange(percentage)
                    onSaveTipPercentage(percentage)
                }
            )

            // Rounding Preference
            RoundingDropdown(
                selectedMode = uiState.roundingMode,
                onModeSelected = { mode ->
                    performHapticFeedback()
                    onRoundingModeChange(mode)
                    onSaveRoundingMode(mode)
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Appearance section
            SettingsSectionHeader("Appearance")

            // Theme selector
            ThemeSelector(
                selectedTheme = uiState.themeMode,
                onThemeSelected = { mode ->
                    performHapticFeedback()
                    onThemeModeChange(mode)
                }
            )

            // Material You dynamic theming (premium, Android only)
            DynamicThemeToggle(
                enabled = uiState.dynamicTheme,
                isPremium = uiState.isPremium,
                onToggle = { enabled ->
                    performHapticFeedback()
                    if (uiState.isPremium) {
                        onDynamicThemeChange(enabled)
                    } else {
                        onPremiumClick()
                    }
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Premium section
            SettingsSectionHeader("Premium")

            PremiumStatusRow(
                isPremium = uiState.isPremium,
                onUnlockClick = {
                    performHapticFeedback()
                    onPremiumClick()
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // History section
            SettingsSectionHeader("History")

            ClearHistoryButton(
                calculationCount = uiState.calculationHistory.size,
                onClearHistory = {
                    performHapticFeedback()
                    onClearHistory()
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // About section
            SettingsSectionHeader("About")

            AboutSection()

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrencyDropdown(
    selectedCurrency: CurrencyInfo,
    onCurrencySelected: (CurrencyInfo) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        OutlinedTextField(
            value = "${selectedCurrency.symbol} ${selectedCurrency.code} — ${selectedCurrency.name}",
            onValueChange = {},
            readOnly = true,
            label = { Text("Default Currency") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
                .semantics { contentDescription = "Default currency: ${selectedCurrency.name}" }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            SUPPORTED_CURRENCIES.forEach { currency ->
                DropdownMenuItem(
                    text = {
                        Text("${currency.symbol} ${currency.code} — ${currency.name}")
                    },
                    onClick = {
                        onCurrencySelected(currency)
                        expanded = false
                    },
                    trailingIcon = if (currency.code == selectedCurrency.code) {
                        {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else null
                )
            }
        }
    }
}

@Composable
private fun TipPercentageSlider(
    tipPercentage: Int,
    onTipPercentageChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Default Tip",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "$tipPercentage%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value = tipPercentage.toFloat(),
            onValueChange = { onTipPercentageChange(it.roundToInt()) },
            valueRange = 0f..50f,
            steps = 49,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Default tip percentage slider: $tipPercentage percent" }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("0%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("50%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoundingDropdown(
    selectedMode: RoundingMode,
    onModeSelected: (RoundingMode) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        OutlinedTextField(
            value = selectedMode.getDisplayName(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Rounding Preference") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
                .semantics { contentDescription = "Rounding preference: ${selectedMode.getDisplayName()}" }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            RoundingMode.entries.forEach { mode ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(mode.getDisplayName())
                            Text(
                                text = mode.getExample(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    onClick = {
                        onModeSelected(mode)
                        expanded = false
                    },
                    trailingIcon = if (mode == selectedMode) {
                        {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else null
                )
            }
        }
    }
}

@Composable
private fun ThemeSelector(
    selectedTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Theme",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            ThemeMode.entries.forEachIndexed { index, mode ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = ThemeMode.entries.size),
                    onClick = { onThemeSelected(mode) },
                    selected = mode == selectedTheme,
                    modifier = Modifier.semantics { contentDescription = "Theme: ${mode.getDisplayName()}" }
                ) {
                    Text(mode.getDisplayName())
                }
            }
        }
    }
}

@Composable
private fun DynamicThemeToggle(
    enabled: Boolean,
    isPremium: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!enabled) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Material You Dynamic Colors",
                    style = MaterialTheme.typography.bodyLarge
                )
                if (!isPremium) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Premium feature",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Text(
                text = "Android 12+ wallpaper-based theme",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = enabled && isPremium,
            onCheckedChange = { onToggle(it) },
            enabled = isPremium,
            modifier = Modifier.semantics { contentDescription = "Material You dynamic colors toggle" }
        )
    }
}

@Composable
private fun PremiumStatusRow(
    isPremium: Boolean,
    onUnlockClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (!isPremium) Modifier.clickable { onUnlockClick() } else Modifier)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Premium Status",
                style = MaterialTheme.typography.bodyLarge
            )
            AnimatedVisibility(
                visible = true,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Text(
                    text = if (isPremium) "Premium unlocked" else "Free tier — tap to upgrade",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isPremium) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (isPremium) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Premium active",
                tint = MaterialTheme.colorScheme.primary
            )
        } else {
            FilledTonalButton(
                onClick = onUnlockClick
            ) {
                Text("Unlock Premium")
            }
        }
    }
}

@Composable
private fun ClearHistoryButton(
    calculationCount: Int,
    onClearHistory: () -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = calculationCount > 0) { showConfirmDialog = true }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Clear History",
                style = MaterialTheme.typography.bodyLarge,
                color = if (calculationCount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = if (calculationCount > 0) "$calculationCount saved calculations" else "No calculations to clear",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (calculationCount > 0) {
            OutlinedButton(
                onClick = { showConfirmDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Clear All")
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Clear History?") },
            text = { Text("This will permanently delete all $calculationCount saved calculations. This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearHistory()
                        showConfirmDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun AboutSection() {
    val appVersion = remember { getAppVersion() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        AboutRow(label = "App Version", value = appVersion)
        AboutRow(label = "Privacy Policy", value = "View")
        AboutRow(label = "Terms of Service", value = "View")
        AboutRow(label = "Rate QuickTip", value = "")
        AboutRow(label = "Share QuickTip", value = "")
        AboutRow(label = "Open Source", value = "GitHub")
    }
}

@Composable
private fun AboutRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Platform-specific link handler would go here */ }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.semantics { contentDescription = label }
        )
        if (value.isNotEmpty()) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
