# Contributing to QuickTip

First off, thank you for considering contributing to QuickTip! 🎉

QuickTip is an open-source project, and we welcome contributions from the community. Whether it's a bug report, feature request, documentation improvement, or code contribution, all help is appreciated.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How Can I Contribute?](#how-can-i-contribute)
  - [Reporting Bugs](#reporting-bugs)
  - [Suggesting Features](#suggesting-features)
  - [Code Contributions](#code-contributions)
- [Development Setup](#development-setup)
- [Coding Guidelines](#coding-guidelines)
- [Commit Messages](#commit-messages)
- [Pull Request Process](#pull-request-process)
- [Testing](#testing)

---

## Code of Conduct

This project adheres to a simple code of conduct: **Be respectful, be constructive, be collaborative.**

- Use welcoming and inclusive language
- Be respectful of differing viewpoints and experiences
- Gracefully accept constructive criticism
- Focus on what is best for the community and the project

---

## How Can I Contribute?

### Reporting Bugs

Before creating a bug report, please check the [existing issues](https://github.com/ofekyariv/quicktip/issues) to avoid duplicates.

**How to Submit a Good Bug Report:**

1. **Use a clear and descriptive title**
2. **Describe the exact steps to reproduce the problem**
3. **Provide specific examples** (screenshots, code snippets, etc.)
4. **Describe the behavior you observed** and what you expected
5. **Include system information**:
   - QuickTip version
   - Platform (Android/iOS)
   - Device/Simulator model
   - OS version
   - Kotlin/Compose versions

**Bug Report Template:**

```markdown
### Description
A clear description of the bug

### Steps to Reproduce
1. Launch app
2. Enter bill amount: $100
3. Select 20% tip
4. Tap "Split Between" and set to 0 people
5. Observe crash

### Expected Behavior
App should prevent setting 0 people or show an error message

### Actual Behavior
App crashes with division by zero error

### Screenshots
[If applicable]

### Environment
- QuickTip Version: 1.0.0
- Platform: Android
- Device: Pixel 8 Pro
- OS: Android 15
- Kotlin: 2.1.0
```

### Suggesting Features

We love new ideas! Before suggesting a feature:

1. **Check existing issues** to see if it's already proposed
2. **Consider if it fits the project's scope** - QuickTip aims to be a simple, fast tip calculator
3. **Explain the use case** - Why is this feature valuable?

**Feature Request Template:**

```markdown
### Feature Description
A clear description of the feature

### Use Case
Why would this be useful? Who would benefit?

### Proposed Solution
How should it work? (UI mockups welcome!)

### Alternatives Considered
What other approaches did you think about?

### Additional Context
Any other relevant information
```

### Code Contributions

We welcome code contributions! Here's how to get started:

1. **Fork the repository**
2. **Clone your fork** locally
3. **Create a feature branch** from `main`
4. **Make your changes**
5. **Test thoroughly**
6. **Commit with clear messages**
7. **Push to your fork**
8. **Open a Pull Request**

---

## Development Setup

### Prerequisites

- **JDK 17+** - [Download](https://adoptium.net/)
- **Android Studio** - [Download](https://developer.android.com/studio)
- **Xcode 15+** (macOS only) - [Download](https://developer.apple.com/xcode/)
- **Kotlin Multiplatform Mobile plugin** - Install from Android Studio

### Initial Setup

```bash
# Clone your fork
git clone https://github.com/YOUR_USERNAME/quicktip.git
cd quicktip

# Add upstream remote
git remote add upstream https://github.com/ofekyariv/quicktip.git

# Create a feature branch
git checkout -b feature/my-awesome-feature

# Build the project
./gradlew build

# Run tests
./gradlew test
```

### Running the App

**Android:**
```bash
./gradlew :composeApp:installDebug
# Or run from Android Studio
```

**iOS:**
```bash
cd iosApp
xcodebuild -workspace iosApp.xcworkspace -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15' build
# Or open in Xcode
```

---

## Coding Guidelines

### Kotlin Style

Follow the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html):

- **Indentation**: 4 spaces (no tabs)
- **Line length**: Aim for 100 characters, max 120
- **Naming**:
  - Classes: `PascalCase`
  - Functions/variables: `camelCase`
  - Constants: `UPPER_SNAKE_CASE`
- **File structure**:
  ```kotlin
  // 1. Package declaration
  package com.ofekyariv.quicktip.ui
  
  // 2. Imports (sorted)
  import androidx.compose.runtime.*
  import kotlinx.coroutines.flow.*
  
  // 3. Class/functions
  @Composable
  fun MainScreen() {
      // ...
  }
  ```

### Compose Best Practices

- **Use `remember` for state** that shouldn't recompose unnecessarily
- **Extract complex composables** into separate functions
- **Use `@Stable` and `@Immutable`** for data classes used in Compose
- **Avoid side effects in composition** - use `LaunchedEffect`, `DisposableEffect`, etc.
- **Name composables clearly**: `TipCalculatorCard`, `CurrencyPicker`, etc.

### Architecture Patterns

QuickTip follows **Clean Architecture** principles:

- **UI Layer** (`ui/`): Compose screens and components (presentation only)
- **ViewModel Layer** (`viewmodel/`): State management and UI logic
- **Data Layer** (`data/`): Business logic, models, repositories
- **DI Layer** (`di/`): Koin modules for dependency injection

**Key Rules:**
- UI should **not** contain business logic
- ViewModels should **not** import Compose
- Data models should be **immutable**
- Use **expect/actual** for platform-specific code

---

## Commit Messages

Write clear, descriptive commit messages following [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <short description>

<optional longer description>

<optional footer>
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, no logic change)
- `refactor`: Code refactoring (no feature change)
- `test`: Adding or updating tests
- `chore`: Build tasks, dependency updates, etc.

**Examples:**

```bash
# Good
feat(ui): add currency selector dropdown
fix(calc): prevent division by zero in bill splitting
docs(readme): update build instructions for Xcode 15
test(calc): add unit tests for rounding modes

# Bad
update stuff
fix bug
changes
```

---

## Pull Request Process

1. **Update documentation** if you've added features or changed behavior
2. **Add tests** for new functionality
3. **Ensure all tests pass** (`./gradlew test`)
4. **Update CHANGELOG.md** under `[Unreleased]`
5. **Fill out the PR template** completely
6. **Link related issues** (e.g., "Closes #42")
7. **Request review** from maintainers

### Pull Request Template

```markdown
## Description
Brief description of what this PR does

## Type of Change
- [ ] Bug fix (non-breaking change)
- [ ] New feature (non-breaking change)
- [ ] Breaking change
- [ ] Documentation update

## Related Issue
Closes #[issue number]

## How Has This Been Tested?
- [ ] Unit tests
- [ ] Manual testing on Android emulator
- [ ] Manual testing on iOS simulator
- [ ] Tested on physical device

## Checklist
- [ ] My code follows the project's style guidelines
- [ ] I have performed a self-review
- [ ] I have commented complex code
- [ ] I have updated documentation
- [ ] I have added tests that prove my fix/feature works
- [ ] All new and existing tests pass
- [ ] I have updated CHANGELOG.md

## Screenshots (if applicable)
[Add screenshots or GIFs here]
```

---

## Testing

### Unit Tests

All business logic **must** have unit tests.

```kotlin
// Example: TipCalculatorTest.kt
class TipCalculatorTest {
    @Test
    fun `calculate tip with 20 percent returns correct amount`() {
        val result = TipCalculator.calculate(
            billAmount = 100.0,
            tipPercentage = 20.0,
            numberOfPeople = 1,
            roundingMode = RoundingMode.None
        )
        
        assertEquals(20.0, result.tipAmount, 0.01)
        assertEquals(120.0, result.total, 0.01)
    }
}
```

Run tests:
```bash
./gradlew :composeApp:testDebugUnitTest
```

### Manual Testing

Before submitting a PR, manually test:

1. **Core functionality**:
   - Enter various bill amounts
   - Test all tip percentages
   - Test bill splitting (1-20 people)
   - Test all currencies
   - Test all rounding modes

2. **Edge cases**:
   - $0 bill
   - Very large bills ($999,999)
   - Very small bills ($0.01)
   - 0% tip
   - 50% tip
   - Single person vs. 20 people

3. **UI/UX**:
   - Light and dark mode
   - Portrait and landscape
   - Phone and tablet layouts
   - Smooth animations
   - No visual glitches

---

## Project Structure Reference

```
composeApp/src/commonMain/kotlin/com/ofekyariv/quicktip/
├── ui/                          # Compose UI
│   ├── screens/
│   │   └── MainScreen.kt        # Main calculator screen
│   ├── components/              # Reusable UI components
│   └── theme/                   # Material 3 theme
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── viewmodel/                   # State management
│   ├── TipViewModel.kt
│   └── TipUiState.kt
├── data/                        # Business logic
│   ├── models/                  # Data models
│   │   ├── TipCalculation.kt
│   │   ├── CurrencyInfo.kt
│   │   ├── RoundingMode.kt
│   │   └── RegionSettings.kt
│   ├── TipCalculator.kt         # Core calculation engine
│   └── CurrencyData.kt          # Currency definitions
├── di/                          # Dependency injection
│   └── AppModule.kt             # Koin module
└── App.kt                       # Root composable

composeApp/src/commonTest/kotlin/
└── com/ofekyariv/quicktip/
    └── data/
        └── TipCalculatorTest.kt  # Unit tests
```

---

## Questions?

- **General questions**: Open a [Discussion](https://github.com/ofekyariv/quicktip/discussions)
- **Bug reports**: Open an [Issue](https://github.com/ofekyariv/quicktip/issues)
- **Security issues**: Email ofekyariv28@gmail.com (do not open public issue)

---

## Recognition

Contributors will be recognized in:
- Project README (Contributors section)
- Release notes (for significant contributions)
- GitHub contributor graph

Thank you for helping make QuickTip better! 🙏
