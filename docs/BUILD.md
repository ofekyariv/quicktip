# Build Guide

Comprehensive build instructions for QuickTip on Android and iOS.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Project Setup](#project-setup)
- [Building for Android](#building-for-android)
- [Building for iOS](#building-for-ios)
- [Common Build Issues](#common-build-issues)
- [Advanced Configuration](#advanced-configuration)

---

## Prerequisites

### Required Software

| Tool | Version | Download Link |
|------|---------|---------------|
| JDK | 17 or higher | [Adoptium](https://adoptium.net/) |
| Android Studio | 2024.1+ | [Download](https://developer.android.com/studio) |
| Xcode | 15.0+ | [Mac App Store](https://apps.apple.com/app/xcode/id497799835) |
| Kotlin | 2.1.0 | Bundled with Android Studio |
| Gradle | 8.10 | Bundled with project (gradlew) |

### Android Studio Plugins

Install these plugins from **Preferences → Plugins**:

1. **Kotlin Multiplatform Mobile** - Essential for KMP development
2. **Compose Multiplatform IDE Support** (optional but recommended)

### Environment Variables

Add to your shell profile (`.zshrc`, `.bashrc`, etc.):

```bash
# Java (adjust path based on your installation)
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home

# Android SDK
export ANDROID_HOME=$HOME/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/emulator
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/tools/bin

# iOS (macOS only)
export PATH=$PATH:/Applications/Xcode.app/Contents/Developer/usr/bin
```

Reload your shell:
```bash
source ~/.zshrc  # or ~/.bashrc
```

---

## Project Setup

### 1. Clone the Repository

```bash
# HTTPS
git clone https://github.com/ofekyariv/quicktip.git

# SSH
git clone git@github.com:ofekyariv/quicktip.git

cd quicktip
```

### 2. Verify Gradle Wrapper

```bash
./gradlew --version

# Expected output:
# Gradle 8.10
# Kotlin: 2.1.0
# JVM: 17.x.x
```

### 3. Sync Dependencies

```bash
./gradlew clean build

# Or in Android Studio:
# File → Sync Project with Gradle Files
```

---

## Building for Android

### Option 1: Android Studio (Recommended)

1. **Open Project**
   - Launch Android Studio
   - Select "Open" and choose the `quicktip` folder

2. **Wait for Gradle Sync**
   - Android Studio will automatically sync Gradle
   - This may take 2-5 minutes on first run

3. **Select Run Configuration**
   - Top toolbar: Select `composeApp` from dropdown
   - Target: Choose emulator or connected device

4. **Run**
   - Click green play button (▶️) or press `Ctrl+R` (Windows/Linux) / `⌘R` (Mac)
   - App will build and launch

### Option 2: Command Line

#### Build APK

```bash
# Debug APK
./gradlew :composeApp:assembleDebug

# Output: composeApp/build/outputs/apk/debug/composeApp-debug.apk

# Release APK (unsigned)
./gradlew :composeApp:assembleRelease
```

#### Install on Device/Emulator

```bash
# Install debug build
./gradlew :composeApp:installDebug

# Launch manually
adb shell am start -n com.ofekyariv.quicktip/.MainActivity
```

#### Build & Run in One Command

```bash
./gradlew :composeApp:installDebug && adb shell am start -n com.ofekyariv.quicktip/.MainActivity
```

### Android Emulator Setup

1. **Create AVD (Android Virtual Device)**
   ```bash
   # List available system images
   sdkmanager --list | grep system-images
   
   # Download image (example: API 34)
   sdkmanager "system-images;android-34;google_apis;arm64-v8a"
   
   # Create AVD
   avdmanager create avd -n Pixel_8_API_34 -k "system-images;android-34;google_apis;arm64-v8a" -d pixel_8
   ```

2. **Launch Emulator**
   ```bash
   emulator -avd Pixel_8_API_34
   ```

3. **Or use Android Studio**
   - Tools → Device Manager → Create Device

---

## Building for iOS

### Option 1: Xcode (Recommended)

1. **Build Kotlin Framework**
   ```bash
   ./gradlew :composeApp:embedAndSignAppleFrameworkForXcode
   ```

2. **Open Xcode Project**
   ```bash
   open iosApp/iosApp.xcworkspace
   ```
   
   ⚠️ **Important**: Open `.xcworkspace`, **not** `.xcodeproj`

3. **Select Target**
   - Top toolbar: Select `iosApp` scheme
   - Device: Choose simulator (e.g., "iPhone 15") or physical device

4. **Run**
   - Click play button (▶️) or press `⌘R`
   - App will build and launch

### Option 2: Command Line

#### Build for Simulator

```bash
# Build Kotlin framework
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64

# Build iOS app
cd iosApp
xcodebuild -workspace iosApp.xcworkspace \
  -scheme iosApp \
  -configuration Debug \
  -destination 'platform=iOS Simulator,name=iPhone 15' \
  clean build
```

#### Install on Simulator

```bash
# Boot simulator
xcrun simctl boot "iPhone 15"

# Install app
xcrun simctl install booted ./build/Debug-iphonesimulator/iosApp.app

# Launch app
xcrun simctl launch booted com.ofekyariv.quicktip
```

#### Build for Device

```bash
./gradlew :composeApp:linkDebugFrameworkIosArm64

cd iosApp
xcodebuild -workspace iosApp.xcworkspace \
  -scheme iosApp \
  -configuration Debug \
  -destination 'generic/platform=iOS' \
  -allowProvisioningUpdates \
  clean build
```

### iOS Simulator Management

```bash
# List available simulators
xcrun simctl list devices

# Create new simulator
xcrun simctl create "iPhone 15 Pro" "iPhone 15 Pro" "iOS17.2"

# Delete simulator
xcrun simctl delete "iPhone 15 Pro"

# Erase simulator data
xcrun simctl erase "iPhone 15"
```

---

## Running Tests

### Unit Tests (Shared Code)

```bash
# All tests
./gradlew test

# Specific module
./gradlew :composeApp:testDebugUnitTest

# With detailed output
./gradlew test --info

# Generate test report
./gradlew test
open composeApp/build/reports/tests/test/index.html
```

### Android Instrumented Tests

```bash
# Run on connected device/emulator
./gradlew :composeApp:connectedAndroidTest

# Specific test class
./gradlew :composeApp:connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.ofekyariv.quicktip.MainScreenTest
```

### iOS Tests (XCTest)

```bash
cd iosApp
xcodebuild test \
  -workspace iosApp.xcworkspace \
  -scheme iosApp \
  -destination 'platform=iOS Simulator,name=iPhone 15'
```

---

## Common Build Issues

### Issue 1: Gradle Sync Failed

**Error**: `Unable to load project`

**Solution**:
```bash
# Clear Gradle cache
rm -rf ~/.gradle/caches/
./gradlew clean --refresh-dependencies

# In Android Studio:
# File → Invalidate Caches → Invalidate and Restart
```

### Issue 2: JDK Version Mismatch

**Error**: `Unsupported Java version`

**Solution**:
```bash
# Check current Java version
java -version

# Set JAVA_HOME to JDK 17
export JAVA_HOME=/path/to/jdk-17

# Or use jEnv to manage multiple JDK versions
brew install jenv
jenv add /Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home
jenv global 17
```

### Issue 3: iOS Framework Not Found

**Error**: `Framework not found ComposeApp`

**Solution**:
```bash
# Rebuild Kotlin framework
./gradlew :composeApp:embedAndSignAppleFrameworkForXcode

# Clean Xcode build
cd iosApp
xcodebuild clean -workspace iosApp.xcworkspace -scheme iosApp
```

### Issue 4: Android SDK Not Found

**Error**: `SDK location not found`

**Solution**:
```bash
# Create local.properties
echo "sdk.dir=$ANDROID_HOME" > local.properties

# Or set in Android Studio:
# File → Project Structure → SDK Location
```

### Issue 5: Kotlin Multiplatform Plugin Missing

**Error**: `The Kotlin Multiplatform plugin is not configured`

**Solution**:
1. Android Studio → Preferences → Plugins
2. Search "Kotlin Multiplatform Mobile"
3. Install and restart IDE

---

## Advanced Configuration

### Build Variants (Android)

```kotlin
// composeApp/build.gradle.kts
android {
    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            isDebuggable = true
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}
```

Build specific variant:
```bash
./gradlew :composeApp:assembleDebug    # Debug build
./gradlew :composeApp:assembleRelease  # Release build
```

### Custom Gradle Properties

Create `gradle.properties` in project root:

```properties
# Performance
org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=1024m
org.gradle.parallel=true
org.gradle.caching=true

# Kotlin
kotlin.code.style=official
kotlin.incremental=true
kotlin.incremental.multiplatform=true

# Android
android.useAndroidX=true
android.nonTransitiveRClass=true
```

### Signing Configuration (Android Release)

Create `keystore.properties` (add to `.gitignore`):

```properties
storeFile=/path/to/keystore.jks
storePassword=your_keystore_password
keyAlias=your_key_alias
keyPassword=your_key_password
```

Update `build.gradle.kts`:

```kotlin
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

---

## Continuous Integration

### GitHub Actions Example

```yaml
# .github/workflows/build.yml
name: Build QuickTip

on: [push, pull_request]

jobs:
  build-android:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
      - name: Build Android
        run: ./gradlew :composeApp:assembleDebug
      - name: Run Tests
        run: ./gradlew test

  build-ios:
    runs-on: macos-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
      - name: Build iOS Framework
        run: ./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
      - name: Build iOS App
        run: |
          cd iosApp
          xcodebuild -workspace iosApp.xcworkspace \
            -scheme iosApp \
            -destination 'platform=iOS Simulator,name=iPhone 15' \
            clean build
```

---

## Resources

- [Kotlin Multiplatform Documentation](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform Docs](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Android Developer Guide](https://developer.android.com/)
- [iOS Developer Documentation](https://developer.apple.com/documentation/)
- [Gradle Build Tool](https://docs.gradle.org/)

---

**Happy Building!** 🚀

For issues not covered here, please [open an issue](https://github.com/ofekyariv/quicktip/issues).
