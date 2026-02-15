# QuickTip Architecture

Technical architecture documentation for QuickTip, a Kotlin Multiplatform tip calculator app.

## Table of Contents

- [Overview](#overview)
- [Architecture Layers](#architecture-layers)
- [Module Structure](#module-structure)
- [Data Flow](#data-flow)
- [State Management](#state-management)
- [Platform-Specific Code](#platform-specific-code)
- [Design Patterns](#design-patterns)
- [Testing Strategy](#testing-strategy)

---

## Overview

QuickTip follows **Clean Architecture** principles adapted for Kotlin Multiplatform. The architecture is designed to:

- **Maximize code sharing** between Android and iOS (95%+ shared code)
- **Maintain separation of concerns** (UI, business logic, data)
- **Support testability** (unit tests for all business logic)
- **Enable scalability** (easy to add features like history, settings, etc.)

### Technology Stack

| Layer | Technology |
|-------|-----------|
| **UI** | Compose Multiplatform |
| **State Management** | Kotlin Flow + StateFlow |
| **Dependency Injection** | Koin |
| **Async Programming** | Kotlin Coroutines |
| **Testing** | kotlin.test |

---

## Architecture Layers

```
┌─────────────────────────────────────────────┐
│            UI Layer (Compose)               │
│  MainScreen, Components, Theme              │
└───────────────┬─────────────────────────────┘
                │ User Events
                ↓
┌─────────────────────────────────────────────┐
│       ViewModel Layer (State Mgmt)          │
│  TipViewModel, TipUiState                   │
└───────────────┬─────────────────────────────┘
                │ Business Logic Calls
                ↓
┌─────────────────────────────────────────────┐
│         Data Layer (Domain Logic)           │
│  TipCalculator, Models, CurrencyData        │
└───────────────┬─────────────────────────────┘
                │ Platform APIs (future)
                ↓
┌─────────────────────────────────────────────┐
│      Platform Layer (Android/iOS)           │
│  expect/actual, Platform-specific code      │
└─────────────────────────────────────────────┘
```

### 1. UI Layer (`ui/`)

**Responsibility**: Render UI and handle user interactions.

**Components**:
- `MainScreen.kt` - Main calculator screen
- `theme/` - Material 3 theme (colors, typography, shapes)
- Future: `HistoryScreen.kt`, `SettingsScreen.kt`, etc.

**Rules**:
- ✅ **DO**: Use Compose for all UI
- ✅ **DO**: Observe ViewModel state via `collectAsState()`
- ✅ **DO**: Handle UI events by calling ViewModel functions
- ❌ **DON'T**: Include business logic in composables
- ❌ **DON'T**: Directly access data layer

**Example**:
```kotlin
@Composable
fun MainScreen(viewModel: TipViewModel = koinInject()) {
    val state by viewModel.uiState.collectAsState()
    
    // UI reacts to state
    Text("Total: ${state.calculation.total}")
    
    // User events call ViewModel
    Button(onClick = { viewModel.calculateTip() }) {
        Text("Calculate")
    }
}
```

### 2. ViewModel Layer (`viewmodel/`)

**Responsibility**: Manage UI state and coordinate business logic.

**Components**:
- `TipViewModel.kt` - Main screen state management
- `TipUiState.kt` - Data class representing UI state

**Rules**:
- ✅ **DO**: Expose `StateFlow<UiState>` for UI to observe
- ✅ **DO**: Handle user events (button clicks, input changes)
- ✅ **DO**: Call data layer for calculations
- ✅ **DO**: Transform domain models to UI-friendly state
- ❌ **DON'T**: Import Compose libraries (keep platform-agnostic)
- ❌ **DON'T**: Perform complex calculations (delegate to data layer)

**Example**:
```kotlin
class TipViewModel(
    private val calculator: TipCalculator
) : ViewModel() {
    private val _uiState = MutableStateFlow(TipUiState())
    val uiState: StateFlow<TipUiState> = _uiState.asStateFlow()
    
    fun updateBillAmount(amount: Double) {
        _uiState.update { it.copy(billAmount = amount) }
        recalculate()
    }
    
    private fun recalculate() {
        val result = calculator.calculate(
            billAmount = _uiState.value.billAmount,
            tipPercentage = _uiState.value.tipPercentage,
            numberOfPeople = _uiState.value.numberOfPeople,
            roundingMode = _uiState.value.roundingMode
        )
        _uiState.update { it.copy(calculation = result) }
    }
}
```

### 3. Data Layer (`data/`)

**Responsibility**: Business logic, data models, calculations.

**Components**:
- `models/` - Data classes (`TipCalculation`, `CurrencyInfo`, `RoundingMode`, `RegionSettings`)
- `TipCalculator.kt` - Core calculation engine
- `CurrencyData.kt` - Currency definitions and defaults
- Future: `repositories/`, `database/`

**Rules**:
- ✅ **DO**: Keep models immutable (`data class`, `val` properties)
- ✅ **DO**: Write pure functions (no side effects)
- ✅ **DO**: Use precise decimal arithmetic (`BigDecimal` for currency)
- ✅ **DO**: Write comprehensive unit tests
- ❌ **DON'T**: Depend on UI or ViewModel
- ❌ **DON'T**: Use platform-specific APIs directly

**Example**:
```kotlin
object TipCalculator {
    fun calculate(
        billAmount: Double,
        tipPercentage: Double,
        numberOfPeople: Int,
        roundingMode: RoundingMode
    ): TipCalculation {
        require(billAmount >= 0) { "Bill amount must be non-negative" }
        require(tipPercentage >= 0) { "Tip percentage must be non-negative" }
        require(numberOfPeople > 0) { "Number of people must be positive" }
        
        val tipAmount = billAmount * (tipPercentage / 100.0)
        val total = billAmount + tipAmount
        val roundedTotal = applyRounding(total, roundingMode)
        val perPerson = roundedTotal / numberOfPeople
        
        return TipCalculation(
            subtotal = billAmount,
            tipAmount = roundedTotal - billAmount,
            total = roundedTotal,
            perPersonAmount = perPerson
        )
    }
}
```

### 4. Platform Layer (`androidMain/`, `iosMain/`)

**Responsibility**: Platform-specific implementations.

**Current**: Not heavily used (most code is shared)

**Future Uses**:
- AdMob initialization (Android vs. iOS)
- Firebase setup
- Number formatting (locale-specific)
- IAP (Google Play Billing vs. StoreKit)
- Analytics (Firebase Android vs. iOS)

**Pattern: expect/actual**:

```kotlin
// commonMain
expect fun formatCurrency(amount: Double, currencyCode: String): String

// androidMain
actual fun formatCurrency(amount: Double, currencyCode: String): String {
    return NumberFormat.getCurrencyInstance(Locale.US).apply {
        currency = Currency.getInstance(currencyCode)
    }.format(amount)
}

// iosMain
actual fun formatCurrency(amount: Double, currencyCode: String): String {
    val formatter = NSNumberFormatter()
    formatter.numberStyle = NSNumberFormatterCurrencyStyle
    formatter.currencyCode = currencyCode
    return formatter.stringFromNumber(NSNumber(amount)) ?: ""
}
```

---

## Module Structure

```
composeApp/
├── src/
│   ├── commonMain/              # Shared code (95%)
│   │   └── kotlin/com/ofekyariv/quicktip/
│   │       ├── ui/              # UI Layer
│   │       │   ├── screens/
│   │       │   │   └── MainScreen.kt
│   │       │   ├── components/
│   │       │   │   └── (future: TipPercentageChips.kt, etc.)
│   │       │   └── theme/
│   │       │       ├── Color.kt
│   │       │       ├── Theme.kt
│   │       │       └── Type.kt
│   │       ├── viewmodel/       # ViewModel Layer
│   │       │   ├── TipViewModel.kt
│   │       │   └── TipUiState.kt
│   │       ├── data/            # Data Layer
│   │       │   ├── models/
│   │       │   │   ├── TipCalculation.kt
│   │       │   │   ├── CurrencyInfo.kt
│   │       │   │   ├── RoundingMode.kt
│   │       │   │   └── RegionSettings.kt
│   │       │   ├── TipCalculator.kt
│   │       │   └── CurrencyData.kt
│   │       ├── di/              # DI Layer
│   │       │   └── AppModule.kt
│   │       └── App.kt           # Root composable
│   ├── androidMain/             # Android-specific (5%)
│   │   └── kotlin/com/ofekyariv/quicktip/
│   │       └── (future: platform implementations)
│   ├── iosMain/                 # iOS-specific (5%)
│   │   └── kotlin/com/ofekyariv/quicktip/
│   │       └── (future: platform implementations)
│   └── commonTest/              # Shared tests
│       └── kotlin/com/ofekyariv/quicktip/
│           └── data/
│               └── TipCalculatorTest.kt
```

---

## Data Flow

### 1. User Interaction Flow

```
User Input
    ↓
UI Event (e.g., TextField onChange)
    ↓
ViewModel Function (e.g., updateBillAmount)
    ↓
Update State (MutableStateFlow)
    ↓
Call Business Logic (TipCalculator.calculate)
    ↓
Update State with Result
    ↓
StateFlow emits new state
    ↓
UI recomposes with new data
```

### 2. Calculation Flow Example

```kotlin
// 1. User enters bill amount in UI
TextField(
    value = state.billAmount.toString(),
    onValueChange = { viewModel.updateBillAmount(it.toDoubleOrNull() ?: 0.0) }
)

// 2. ViewModel receives update
fun updateBillAmount(amount: Double) {
    _uiState.update { it.copy(billAmount = amount) }
    recalculate()  // Trigger recalculation
}

// 3. ViewModel calls calculator
private fun recalculate() {
    val result = calculator.calculate(
        billAmount = _uiState.value.billAmount,
        tipPercentage = _uiState.value.tipPercentage,
        numberOfPeople = _uiState.value.numberOfPeople,
        roundingMode = _uiState.value.roundingMode
    )
    _uiState.update { it.copy(calculation = result) }
}

// 4. Calculator performs pure calculation
fun calculate(...): TipCalculation {
    // Math operations
    return TipCalculation(...)
}

// 5. UI observes state and recomposes
val state by viewModel.uiState.collectAsState()
Text("Total: ${state.calculation.total}")
```

---

## State Management

### StateFlow Pattern

QuickTip uses **StateFlow** for reactive state management:

```kotlin
class TipViewModel : ViewModel() {
    // Private mutable state (internal)
    private val _uiState = MutableStateFlow(TipUiState())
    
    // Public immutable state (exposed to UI)
    val uiState: StateFlow<TipUiState> = _uiState.asStateFlow()
    
    // State updates are unidirectional
    fun updateTipPercentage(percent: Double) {
        _uiState.update { currentState ->
            currentState.copy(tipPercentage = percent)
        }
    }
}
```

### UI State Design

```kotlin
@Immutable
data class TipUiState(
    val billAmount: Double = 0.0,
    val tipPercentage: Double = 18.0,      // Default 18%
    val numberOfPeople: Int = 1,
    val selectedCurrency: CurrencyInfo = CurrencyData.defaultCurrency(),
    val roundingMode: RoundingMode = RoundingMode.None,
    val calculation: TipCalculation = TipCalculation.EMPTY,
    val isLoading: Boolean = false,
    val error: String? = null
)
```

**Benefits**:
- **Single source of truth** - UI state is centralized
- **Immutability** - State changes are predictable
- **Type-safe** - Compile-time guarantees
- **Testable** - Easy to verify state transitions

---

## Platform-Specific Code

### expect/actual Mechanism

**Pattern**: Declare in `commonMain`, implement in `androidMain` and `iosMain`.

```kotlin
// commonMain/kotlin/.../ PlatformUtils.kt
expect object PlatformUtils {
    fun getDefaultCurrencyCode(): String
    fun formatNumber(value: Double, decimals: Int): String
}

// androidMain/kotlin/.../PlatformUtils.kt
actual object PlatformUtils {
    actual fun getDefaultCurrencyCode(): String {
        return Currency.getInstance(Locale.getDefault()).currencyCode
    }
    
    actual fun formatNumber(value: Double, decimals: Int): String {
        return String.format(Locale.getDefault(), "%.${decimals}f", value)
    }
}

// iosMain/kotlin/.../PlatformUtils.kt
actual object PlatformUtils {
    actual fun getDefaultCurrencyCode(): String {
        return NSLocale.currentLocale.currencyCode ?: "USD"
    }
    
    actual fun formatNumber(value: Double, decimals: Int): String {
        val formatter = NSNumberFormatter()
        formatter.minimumFractionDigits = decimals.toULong()
        formatter.maximumFractionDigits = decimals.toULong()
        return formatter.stringFromNumber(NSNumber(value)) ?: ""
    }
}
```

---

## Design Patterns

### 1. Repository Pattern (Future)

For data persistence (calculation history, settings):

```kotlin
interface CalculationRepository {
    suspend fun saveCalculation(calculation: TipCalculation)
    fun getRecentCalculations(limit: Int): Flow<List<TipCalculation>>
    suspend fun deleteCalculation(id: Long)
}

class CalculationRepositoryImpl(
    private val database: QuickTipDatabase
) : CalculationRepository {
    // SQLDelight implementation
}
```

### 2. Strategy Pattern (Rounding)

```kotlin
sealed class RoundingMode {
    abstract fun round(value: Double): Double
    
    object None : RoundingMode() {
        override fun round(value: Double) = value
    }
    
    object NearestDollar : RoundingMode() {
        override fun round(value: Double) = kotlin.math.round(value)
    }
    
    object NearestHalf : RoundingMode() {
        override fun round(value: Double) = (kotlin.math.round(value * 2) / 2.0)
    }
}
```

### 3. Dependency Injection (Koin)

```kotlin
// AppModule.kt
val appModule = module {
    // Singleton calculator
    single { TipCalculator }
    
    // Scoped ViewModel (lifecycle-aware)
    viewModel { TipViewModel(get()) }
    
    // Future: repositories, database, etc.
}

// App.kt
@Composable
fun App() {
    MaterialTheme {
        val viewModel: TipViewModel = koinInject()
        MainScreen(viewModel)
    }
}
```

---

## Testing Strategy

### Unit Tests (Data Layer)

**Coverage Goal**: 100% of business logic

```kotlin
class TipCalculatorTest {
    @Test
    fun `20% tip on $100 bill equals $20`() {
        val result = TipCalculator.calculate(
            billAmount = 100.0,
            tipPercentage = 20.0,
            numberOfPeople = 1,
            roundingMode = RoundingMode.None
        )
        
        assertEquals(20.0, result.tipAmount, 0.01)
        assertEquals(120.0, result.total, 0.01)
    }
    
    @Test
    fun `splitting $120 among 4 people equals $30 per person`() {
        val result = TipCalculator.calculate(
            billAmount = 100.0,
            tipPercentage = 20.0,
            numberOfPeople = 4,
            roundingMode = RoundingMode.None
        )
        
        assertEquals(30.0, result.perPersonAmount, 0.01)
    }
    
    @Test
    fun `rounding to nearest half dollar works correctly`() {
        val result = TipCalculator.calculate(
            billAmount = 100.0,
            tipPercentage = 18.0,  // $18 tip = $118 total
            numberOfPeople = 1,
            roundingMode = RoundingMode.NearestHalf
        )
        
        // $118 should round to $118.00 (no change)
        assertEquals(118.0, result.total, 0.01)
    }
}
```

### ViewModel Tests (Future)

```kotlin
class TipViewModelTest {
    private lateinit var viewModel: TipViewModel
    
    @BeforeTest
    fun setup() {
        viewModel = TipViewModel(TipCalculator)
    }
    
    @Test
    fun `updating bill amount triggers recalculation`() = runTest {
        viewModel.updateBillAmount(100.0)
        viewModel.updateTipPercentage(20.0)
        
        val state = viewModel.uiState.value
        assertEquals(20.0, state.calculation.tipAmount, 0.01)
    }
}
```

### UI Tests (Maestro)

**See**: `/ui-tests/` directory for Maestro flow definitions

---

## Future Architecture Considerations

### 1. Feature Modules

As the app grows, consider splitting into feature modules:

```
composeApp/
├── feature-calculator/    # Main screen
├── feature-history/       # History screen
├── feature-settings/      # Settings
├── feature-premium/       # IAP
└── core/                  # Shared utilities
```

### 2. Multi-Module Architecture

```
:composeApp (UI)
    ↓
:domain (business logic, interfaces)
    ↓
:data (repositories, database)
```

### 3. Advanced State Management

For complex state, consider:
- **MVI (Model-View-Intent)** pattern
- **Redux-like** state container
- **Decompose** navigation library

---

## Resources

- [Kotlin Multiplatform Architecture](https://kotlinlang.org/docs/multiplatform-mobile-understand-project-structure.html)
- [Compose Multiplatform Best Practices](https://github.com/JetBrains/compose-multiplatform/blob/master/tutorials/Getting_Started/README.md)
- [Clean Architecture by Uncle Bob](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Koin Documentation](https://insert-koin.io/docs/reference/koin-mp/kmp)

---

**Questions?** Open an issue or discussion on GitHub!
