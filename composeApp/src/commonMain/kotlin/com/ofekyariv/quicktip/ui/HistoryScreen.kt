package com.ofekyariv.quicktip.ui

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ofekyariv.quicktip.ads.AdManager
import com.ofekyariv.quicktip.data.models.TipCalculation
import com.ofekyariv.quicktip.viewmodel.HistoryViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    onPremiumClick: () -> Unit
) {
    val viewModel: HistoryViewModel = koinInject()
    val adManager: AdManager = koinInject()
    val uiState by viewModel.uiState.collectAsState()

    // Clear confirmation dialog
    if (uiState.showClearConfirmDialog) {
        ClearHistoryConfirmDialog(
            onConfirm = {
                viewModel.clearAllHistory()
            },
            onDismiss = {
                viewModel.showClearConfirmDialog(false)
            }
        )
    }

    // Premium bottom sheet
    if (uiState.showPremiumSheet) {
        PremiumSheet(
            onDismiss = { viewModel.showPremiumSheet(false) },
            onPurchase = { /* Handled by TipViewModel via shared state */ },
            onRestore = { /* Handled by TipViewModel via shared state */ },
            onWatchAd = {
                adManager.showRewardedAd {
                    // Reward unlocked — handled by shared settings
                }
                viewModel.showPremiumSheet(false)
            },
            isPurchaseLoading = false,
            iapError = null,
            onRetry = {}
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History") },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.semantics {
                            contentDescription = "Go Back"
                            testTag = "history_back_button"
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (uiState.calculations.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.showClearConfirmDialog(true) },
                            modifier = Modifier.semantics {
                                contentDescription = "Clear All History"
                                testTag = "clear_all_button"
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear All"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Error snackbar-style message
            AnimatedVisibility(
                visible = uiState.error != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                uiState.error?.let { errorMsg ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
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

            // Loading state
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .semantics { testTag = "loading_indicator" }
                )
            }
            // Empty state
            else if (uiState.calculations.isEmpty()) {
                EmptyHistoryState(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(32.dp)
                )
            }
            // History list
            else {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Premium banner (when limit reached)
                    if (uiState.isLimitReached) {
                        PremiumBanner(
                            onUpgradeClick = onPremiumClick,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    // Calculation list
                    HistoryList(
                        calculations = uiState.calculations,
                        isPremium = uiState.isPremium,
                        onDeleteCalculation = { viewModel.deleteCalculation(it) },
                        getCurrencyInfo = { viewModel.getCurrencyInfo(it) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HistoryList(
    calculations: List<TipCalculation>,
    isPremium: Boolean,
    onDeleteCalculation: (Long) -> Unit,
    getCurrencyInfo: (String) -> com.ofekyariv.quicktip.data.models.CurrencyInfo,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .semantics { testTag = "history_list" },
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = calculations,
            key = { it.id }
        ) { calculation ->
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = { dismissValue ->
                    if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                        onDeleteCalculation(calculation.id)
                        true
                    } else {
                        false
                    }
                }
            )

            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = {
                    val color by animateColorAsState(
                        when (dismissState.targetValue) {
                            SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
                            else -> Color.Transparent
                        }
                    )
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(color)
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                },
                enableDismissFromStartToEnd = false
            ) {
                HistoryItem(
                    calculation = calculation,
                    currencyInfo = getCurrencyInfo(calculation.currency),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun HistoryItem(
    calculation: TipCalculation,
    currencyInfo: com.ofekyariv.quicktip.data.models.CurrencyInfo,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.semantics { testTag = "history_item_${calculation.id}" },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Date and time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatTimestamp(calculation.timestamp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${calculation.tipPercentage.toInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Calculation details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    HistoryItemRow(
                        label = "Bill:",
                        value = calculation.formattedBillAmount(currencyInfo)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    HistoryItemRow(
                        label = "Tip:",
                        value = calculation.formattedTipAmount(currencyInfo)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    HistoryItemRow(
                        label = "Total:",
                        value = calculation.formattedTotalAmount(currencyInfo),
                        highlighted = true
                    )
                }

                // Per person (if split)
                if (calculation.numPeople > 1) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Text(
                            text = "${calculation.numPeople} people",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = calculation.formattedPerPersonAmount(currencyInfo),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "per person",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItemRow(
    label: String,
    value: String,
    highlighted: Boolean = false
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = if (highlighted) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodySmall,
            fontWeight = if (highlighted) FontWeight.Bold else FontWeight.Normal,
            color = if (highlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun EmptyHistoryState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { testTag = "empty_state" },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "📜",
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No saved calculations yet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Save calculations from the main screen to see them here",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ClearHistoryConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text("Clear All History?")
        },
        text = {
            Text("This will permanently delete all your saved calculations. This action cannot be undone.")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                modifier = Modifier.semantics { testTag = "confirm_clear_button" }
            ) {
                Text("Clear All", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Formats timestamp to human-readable date/time.
 * Example: "Feb 17, 2026"
 * Simple format without time to avoid datetime library dependency.
 */
expect fun formatTimestamp(timestamp: Long): String
