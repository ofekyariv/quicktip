# QuickTip 💰

A smart, elegant tip calculator with country etiquette guides, bill splitting, and regional rounding. Built with Kotlin Multiplatform for Android and iOS.

[![Google Play](https://img.shields.io/badge/Google%20Play-Coming%20Soon-green)](https://play.google.com/store/apps/details?id=com.ofekyariv.quicktip)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-purple.svg)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.7.1-blue.svg)](https://www.jetbrains.com/lp/compose-multiplatform)

---

## ✨ Features

### Core Functionality
- 💵 **Smart Tip Calculation** - Calculate tips with customizable percentages
- 👥 **Bill Splitting** - Split bills evenly among multiple people
- 🌍 **50+ Country Tipping Guides** - Cultural etiquette and recommended ranges by service type
- 🍽️ **6 Service Types** - Restaurant, Taxi, Salon, Hotel, Delivery, Counter Service
- 🎯 **Regional Rounding** - Smart rounding based on regional preferences
  - US: Round to nearest $0.50 or $1.00
  - Israel: Round to nearest ₪0.10
  - EU: Round to nearest €0.50
  - UK: Round to nearest £0.50
- 🎨 **Material 3 Design** - Beautiful, modern UI following Material Design 3
- 🌓 **Dark Mode** - Full support for light and dark themes

### Tip Presets
- Quick access to common tip percentages: 10%, 15%, 18%, 20%, 25%
- Custom tip percentage slider (0-50%)
- Default tip percentage based on region

### Coming Soon
- 📊 **Calculation History** - Save and review past calculations (last 5 free, unlimited with premium)
- 💎 **Premium Features** - Unlock advanced features with one-time $0.99 purchase
- 📈 **Analytics** - Firebase Analytics integration
- 📱 **Monetization** - AdMob integration (banner, interstitial, rewarded ads)

---

## 🏗️ Architecture

QuickTip is built using **Kotlin Multiplatform (KMP)** with **Compose Multiplatform** for shared UI across Android and iOS.

### Project Structure

```
quicktip-code/
├── composeApp/               # Shared Compose Multiplatform code
│   └── src/
│       ├── commonMain/       # Shared code (Android + iOS)
│       │   └── kotlin/com/ofekyariv/quicktip/
│       │       ├── ui/       # Compose UI screens and components
│       │       │   ├── MainScreen.kt
│       │       │   └── theme/
│       │       ├── viewmodel/    # State management
│       │       │   ├── TipViewModel.kt
│       │       │   └── TipUiState.kt
│       │       ├── data/     # Business logic & models
│       │       │   ├── models/
│       │       │   │   ├── TipCalculation.kt
│       │       │   │   ├── CurrencyInfo.kt
│       │       │   │   ├── RoundingMode.kt
│       │       │   │   └── RegionSettings.kt
│       │       │   ├── TipCalculator.kt
│       │       │   └── CurrencyData.kt
│       │       ├── di/       # Dependency injection (Koin)
│       │       │   └── AppModule.kt
│       │       └── App.kt    # Root composable
│       ├── androidMain/      # Android-specific code
│       ├── iosMain/          # iOS-specific code
│       └── commonTest/       # Shared unit tests
├── iosApp/                   # iOS app wrapper
└── gradle/                   # Build configuration
```

### Tech Stack

#### Core Frameworks
- **[Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)** - Share code between Android and iOS
- **[Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)** - Declarative UI framework
- **[Kotlin Coroutines & Flow](https://kotlinlang.org/docs/coroutines-overview.html)** - Asynchronous programming

#### Libraries
- **[Koin](https://insert-koin.io/)** - Dependency injection
- **[ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)** - Lifecycle-aware state management
- **[Material 3](https://m3.material.io/)** - Modern Material Design components

#### Future Integrations
- **[SQLDelight](https://cashapp.github.io/sqldelight/)** - Type-safe database (for calculation history)
- **[DataStore](https://developer.android.com/topic/libraries/architecture/datastore)** - Settings persistence
- **[AdMob](https://admob.google.com/)** - Mobile advertising
- **[Firebase Analytics](https://firebase.google.com/products/analytics)** - Usage analytics
- **[Google Play Billing](https://developer.android.com/google/play/billing)** / **[StoreKit](https://developer.apple.com/storekit/)** - In-app purchases

---

## 🚀 Getting Started

### Prerequisites

- **JDK 17 or higher** - [Download JDK](https://adoptium.net/)
- **Android Studio** (for Android development) - [Download](https://developer.android.com/studio)
- **Xcode 15+** (for iOS development, macOS only) - [Download](https://developer.apple.com/xcode/)
- **Kotlin Multiplatform Mobile plugin** - Install from Android Studio plugins

### Build Instructions

#### 1. Clone the repository
```bash
git clone <repository-url>
cd quicktip-code
```

#### 2. Build for Android

**Option A: Command Line**
```bash
./gradlew :composeApp:assembleDebug
```

**Option B: Android Studio**
1. Open the project in Android Studio
2. Select "composeApp" run configuration
3. Click Run (green play button)
4. Choose an emulator or connected device

#### 3. Build for iOS

**Option A: Command Line**
```bash
./gradlew :composeApp:iosSimulatorArm64MainBinariesLinkDebugFrameworkIosSimulatorArm64
cd iosApp
xcodebuild -workspace iosApp.xcworkspace -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' build
```

**Option B: Xcode**
1. Open `iosApp/iosApp.xcworkspace` in Xcode
2. Select target device/simulator
3. Click Run (⌘R)

#### 4. Run Tests
```bash
# Run all tests
./gradlew test

# Run specific module tests
./gradlew :composeApp:testDebugUnitTest
```

---

## 🧪 Testing

### Unit Tests

Unit tests are located in `composeApp/src/commonTest/kotlin/` and test the core business logic:

- **TipCalculator** - Validates tip calculation accuracy
- **RoundingMode** - Ensures regional rounding rules work correctly
- **CurrencyInfo** - Verifies currency data integrity

Run tests with:
```bash
./gradlew :composeApp:testDebugUnitTest
```

### Manual Testing Checklist

- [ ] Enter bill amount ($150)
- [ ] Select tip percentage (18%)
- [ ] Adjust number of people (4)
- [ ] Verify calculations:
  - Subtotal: $150.00
  - Tip (18%): $27.00
  - Total: $177.00
  - Per Person: $44.25
- [ ] Test different currencies (USD, EUR, GBP, ILS)
- [ ] Test rounding modes (None, Nearest Half Dollar, Nearest Dollar)
- [ ] Test edge cases:
  - Zero bill amount
  - Zero tip percentage
  - Single person vs. multiple people
  - Very large bill amounts
  - Very small bill amounts

---

## 🎨 Design Philosophy

QuickTip follows **Material Design 3** principles with:

- **Clear hierarchy** - Important numbers (total, per person) are prominently displayed
- **Accessible design** - High contrast, large touch targets, semantic labels
- **Smooth animations** - Material motion for delightful interactions
- **Adaptive layouts** - Works on phones and tablets, portrait and landscape
- **Regional awareness** - Defaults adapt to user's locale (currency, rounding, tip percentage)

### Color Palette

- **Primary**: Teal Green (#00897B) - Trust, financial stability
- **Secondary**: Amber (#FFC107) - Warmth, generosity
- **Surface**: Dynamic based on theme (light/dark mode)

---

## 📝 Development Roadmap

### ✅ Completed (Units 1-4)
- [x] Project setup with KMP template
- [x] Core calculation engine
- [x] Main screen UI with Material 3
- [x] Currency support (20+ currencies)
- [x] Regional rounding rules
- [x] Tip presets and custom slider
- [x] Bill splitting logic
- [x] ViewModel with StateFlow
- [x] Dependency injection (Koin)
- [x] Unit tests for core logic

### ✅ Completed (Units 1-10)
- [x] Project setup with KMP template
- [x] Core calculation engine
- [x] Main screen UI with Material 3
- [x] Currency support (20+ currencies)
- [x] Regional rounding rules
- [x] Tip presets and custom slider
- [x] Bill splitting logic
- [x] ViewModel with StateFlow
- [x] Dependency injection (Koin)
- [x] Unit tests for core logic
- [x] 50+ country tipping database
- [x] Service type selector (6 types)
- [x] AdMob integration (banner, interstitial, rewarded)
- [x] Firebase Analytics
- [x] Data persistence (SQLDelight + DataStore)
- [x] Calculation history screen
- [x] Premium features (IAP)
- [x] Settings screen
- [x] Error handling and validation
- [x] 15 Maestro UI tests

### 🔮 Future Enhancements (v1.1+)
- [ ] Bill scanning with OCR (ML Kit)
- [ ] Real-time currency conversion
- [ ] Itemized bill splitting (per-item allocation)
- [ ] Social sharing (share calculation as image)
- [ ] Tip etiquette guide by country
- [ ] Apple Watch / Wear OS companion app

---

## 💎 Premium Features

QuickTip offers a **$1.99 one-time purchase** to unlock premium features:

- ✅ **Unlimited calculation history** (free: last 5 only)
- ✅ **Custom tip presets** (save favorite percentages)
- ✅ **Export history to CSV**
- ✅ **No banner ads**
- ✅ **Material You dynamic theming** (Android 12+)
- ✅ **Custom rounding rules**

**Alternative**: Watch a 30-second rewarded ad to unlock premium for 24 hours.

---

## 🤝 Contributing

Contributions are welcome! This is an open-source project maintained by the App Factory team.

### How to Contribute

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable/function names
- Write unit tests for new features
- Update documentation for significant changes

---

## 📄 License

This project is licensed under the **Apache License 2.0** - see the [LICENSE](LICENSE) file for details.

```
Copyright 2026 Ofek Yariv

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

---

## 🙏 Acknowledgments

- **JetBrains** - For Kotlin Multiplatform and Compose Multiplatform
- **Google** - For Material Design 3, Firebase, and AdMob
- **Koin Team** - For the excellent dependency injection framework
- **App Factory** - Build automation and orchestration

---

## 📧 Contact & Support

- **Issues**: [GitHub Issues](https://github.com/ofekyariv/quicktip/issues)
- **Email**: ofekyariv28@gmail.com
- **Privacy Policy**: https://quicktip.app/privacy (placeholder)
- **Terms of Service**: https://quicktip.app/terms (placeholder)

---

## 📊 Project Status

- **Version**: 1.0.0 (in development)
- **Build Status**: ✅ Units 1-4 Complete
- **Target Platforms**: Android (API 24+), iOS (15.0+)
- **Release Date**: TBD (targeting Q1 2026)

---

**Built with ❤️ using Kotlin Multiplatform**
