# 🪙 CryptoNest KMP (Sample)

**CryptoNest KMP** is an educational Kotlin Multiplatform (KMP) project targeting **Android** and **iOS** using **Compose Multiplatform** for shared UI.
It demonstrates modern patterns for cross-platform development, shared logic, and UI composition.

> ⚠️ This repository is for educational purposes only and **not intended for production use**.

---

## 📁 Project Structure

```
composeApp/
├── src/commonMain/kotlin/parinexus/kmp/first
│   ├── core            # Errors, results, navigation, networking, and shared database
│   ├── coins           # Data/domain/presentation layers for the coin list
│   ├── portfolio       # Logic and UI for portfolio management
│   ├── trade           # Buy and sell flows
│   ├── theme           # Shared colors and theming
│   └── di              # Shared Koin modules
├── src/androidMain/... # Android-specific implementations (secrets, DI, Activity)
├── src/iosMain/...     # iOS-specific implementations (secrets, DI, UIKit bridge)
iosApp/                  # Xcode project and iOS configuration
build.gradle.kts         # Gradle settings and dependencies
```

---

## 🧩 Project Overview

* Shared UI built with **Compose Multiplatform** for Android and iOS.
* Route-based navigation (`App.kt`) for moving between portfolio, coin grid, and trade screens.
* **Koin** used for dependency injection with shared and platform-specific modules.
* **Ktor** handles networking with shared configuration (`core/network/HttpClientFactory.kt`) and error-safe helpers (`safeCall`).
* **Room** provides local storage for portfolio data.
* Buy/Sell trade flows implemented via shared UI and view models.
* Platform-specific secrets (`AppSecrets`) injected into the Ktor client.

---

## 🧰 Prerequisites

* **JDK 17** and **Gradle 8.x**
* **Android Studio Hedgehog** (or newer) with Kotlin Multiplatform plugin
* **Xcode 15** for iOS builds
* **API key** from [Coinranking API](https://developers.coinranking.com/api/documentation/coins/coin-details)

---

## 🔑 API Key and Base URL Configuration

You must obtain your **API key** from **[Coinranking Developers](https://developers.coinranking.com/api/documentation/coins/coin-details)**.
Then configure it for both Android and iOS as follows:

### **Android**

1. In the project root, open or create a file named `local.properties`.
2. Add your API credentials:

   ```properties
   API_KEY=your_api_key_here
   BASE_URL=https://api.coinranking.com/v2/
   ```
3. Gradle automatically injects these values into `BuildConfig`, making them accessible via `AppSecrets`.

---

### **iOS**

1. Inside `iosApp/iosApp`, create a file named **`Secrets.plist`**.
2. Add the following XML structure:

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
   <plist version="1.0">
   <dict>
       <key>API_KEY</key>
       <string>your_api_key_here</string>
       <key>BASE_URL</key>
       <string>https://api.coinranking.com/v2/</string>
   </dict>
   </plist>
   ```
3. The iOS `AppSecrets` class loads these values at runtime.

---

## ⚙️ Key Libraries and Technologies

* **Compose Multiplatform** — shared UI across Android/iOS
* **Koin** — dependency injection and view model management
* **Ktor Client** — HTTP networking with Kotlinx serialization
* **Room + SQLite** — local data storage
* **Coil 3** — image loading for both Android and iOS
* **Navigation Compose** — type-safe navigation

---

## 📘 Notes

* All cryptocurrency data is **fictitious** and for demonstration only.
* Validate networking, security, and persistence before using in real-world products.
* The API key and base URL are required for app functionality.

---

Would you like me to make it look even more polished with badges (like `KMP`, `Compose`, `Ktor`, etc.) and emoji headers (📦, 🔐, ⚙️) for GitHub presentation?
