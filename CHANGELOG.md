# Changelog

All notable changes to QuickTip will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added (In Development)
- AdMob integration (banner, interstitial, rewarded ads)
- Firebase Analytics tracking
- Data persistence with SQLDelight
- Calculation history screen
- Premium features (IAP)
- Settings screen
- Error handling and input validation
- Maestro UI tests

## [1.0.0] - 2026-02-15 (In Progress)

### Added
- ✅ Core tip calculation engine with precise decimal handling
- ✅ Bill splitting functionality (1-99 people)
- ✅ Multi-currency support (20+ currencies: USD, EUR, GBP, ILS, INR, JPY, CAD, AUD, CNY, etc.)
- ✅ Regional rounding modes:
  - None (exact calculation)
  - Nearest Dollar (US)
  - Nearest Half Dollar (US)
  - Nearest Shekel Tenth (Israel)
  - Nearest Euro Half (EU)
  - Nearest Pound Half (UK)
- ✅ Tip percentage presets (10%, 15%, 18%, 20%, 25%)
- ✅ Custom tip percentage slider (0-50%)
- ✅ Material 3 design system
- ✅ Light and dark theme support
- ✅ Responsive layouts for phones and tablets
- ✅ ViewModel-based state management with StateFlow
- ✅ Dependency injection with Koin
- ✅ Comprehensive unit tests for calculation logic
- ✅ Project structure and build configuration
- ✅ Complete documentation (README, CHANGELOG, CONTRIBUTING)

### Architecture
- Kotlin Multiplatform (KMP) for shared code
- Compose Multiplatform for shared UI
- Clean Architecture with separation of concerns:
  - UI Layer (Compose screens and components)
  - ViewModel Layer (State management)
  - Data Layer (Business logic and models)
  - DI Layer (Koin modules)

### Technical Details
- **Minimum Android SDK**: API 24 (Android 7.0)
- **Minimum iOS Version**: iOS 15.0
- **Kotlin Version**: 2.1.0
- **Compose Multiplatform**: 1.7.1
- **Gradle**: 8.10

---

## Build Units Completed

### Unit 1: Project Setup & Core Structure ✅
- Initial KMP template configuration
- Package structure created (`com.ofekyariv.quicktip`)
- Koin dependency injection setup
- Material 3 theme configuration
- Gradle dependencies configured
- **Commit**: `414ba2a` - "Unit 1: Setup QuickTip project structure"

### Unit 2: Data Models & Core Logic ✅
- Created `TipCalculation` data class
- Created `CurrencyInfo` model (code, symbol, flag emoji, rounding rules)
- Created `RoundingMode` enum (None, NearestDollar, NearestHalf, etc.)
- Created `RegionSettings` for locale-aware defaults
- Implemented `TipCalculator` with precise calculations
- Created comprehensive `CurrencyData` with 20+ currencies
- Unit tests for calculation engine
- **Commit**: `2fdcdd0` - "Implement Units 2, 3, 4: Data models, Main Screen UI, and ViewModel"

### Unit 3: Main Screen UI ✅
- Material 3 `MainScreen` composable
- Bill amount input with currency selector
- Tip percentage chips (10%, 15%, 18%, 20%, 25%)
- Custom tip slider (0-50%)
- Number of people selector
- Results card showing:
  - Subtotal
  - Tip amount
  - Total
  - Per person amount
- Responsive design for different screen sizes
- Smooth animations and transitions

### Unit 4: ViewModel & State Management ✅
- `TipViewModel` with StateFlow
- `TipUiState` data class for reactive UI
- Real-time calculation updates
- Currency selection handling
- Rounding mode selection
- Number of people adjustment
- Tip percentage updates

### Unit 11: README & Documentation ✅ (Current)
- Comprehensive README.md with:
  - Feature overview
  - Architecture documentation
  - Build instructions
  - Testing guidelines
  - Development roadmap
  - Contributing guidelines
- CHANGELOG.md for version tracking
- CONTRIBUTING.md for contributors
- Documentation complete

---

## Future Versions

### [1.1.0] - Planned
- Bill scanning with OCR (ML Kit)
- Real-time currency conversion API
- Itemized bill splitting
- Custom tip reasons/notes
- Share calculation as image

### [1.2.0] - Planned
- Social features (leaderboard, achievements)
- Tip etiquette guide by country
- Multi-language support (i18n)
- Accessibility improvements (VoiceOver, TalkBack)

### [1.3.0] - Planned
- Subscription tier ($1.99/month)
- Business tier (POS integration)
- Apple Watch / Wear OS app
- Widget support (Android/iOS)

---

## Development Timeline

- **2026-02-15**: Project started, Units 1-4 completed
- **2026-02-15**: Unit 11 (Documentation) completed
- **2026-02-16**: Units 5-10 (target completion)
- **2026-02-22**: v1.0.0 release candidate
- **Q1 2026**: Public beta launch
- **Q2 2026**: v1.0.0 official release

---

## Links

- [GitHub Repository](https://github.com/ofekyariv/quicktip)
- [Issue Tracker](https://github.com/ofekyariv/quicktip/issues)
- [Privacy Policy](https://quicktip.app/privacy) (placeholder)
- [Terms of Service](https://quicktip.app/terms) (placeholder)
