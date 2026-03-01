# DEXPENSES 💎

**DEXPENSES** is a premium personal finance manager for Android that focuses on financial clarity and modern aesthetics. It moves beyond simple expense tracking by introducing the concept of **"Safe to Spend"**—helping you understand exactly how much liquidity you have after accounting for your monthly obligations.

---

## ✨ Key Features

### 🏦 Smart Account Management
- **Air Cash (Liquid)**: Track your cash-on-hand and digital wallet balances.
- **Solid Cash (Fixed)**: Monitor your savings and fixed deposits.
- **Inter-account Transfers**: Effortlessly move money between Air and Solid accounts.
  - **Smart Logging**: Transfers are shown as a single, elegant **Violet** entry in your log (`⇄`) instead of two confusing transactions.

### 🛡️ Financial Safety Net
- **Safe to Spend**: A high-visibility dashboard card that calculates `Total Air Cash - Total EMIs`. 
- **Visual Alerts**: The balance automatically turns **Red** if your liquid cash isn't enough to cover your upcoming EMI obligations, acting as an early warning system.

### 💳 EMI & Debt Tracker
- **Dedicated EMI Manager**: Add, edit, and remove your monthly installments.
- **Lending Tracker**: Flag specific transactions as "Lending" to keep track of money owed to you by friends or family.

### 🎨 Modern "Liquid Glass" UI
- **Glassmorphism Design**: Beautiful translucent cards and buttons with subtle blur effects.
- **Interactive Feedback**: Liquid-style touch animations on all primary dashboard cards and buttons.
- **Color-Coded Insights**:
  - <span style="color:#34C759">●</span> **Green**: Received / Income
  - <span style="color:#FF3B30">●</span> **Red**: Spent / Expense
  - <span style="color:#AF52DE">●</span> **Violet**: Internal Transfers

### 🏠 Home Screen Widget
- Stay updated without opening the app. The **Balance Widget** provides a real-time glance at your current financial status right from your home screen.

---

## 🛠️ Technical Stack

- **Platform**: Android (Min SDK 26)
- **Language**: Java & Kotlin
- **Database**: [Room Persistence Library](https://developer.android.com/training/data-storage/room) for robust offline data management.
- **Concurrency**: ExecutorService for smooth background database operations.
- **UI Architecture**: XML-based Material Design with custom Glassmorphism drawables.

---

## 🚀 Getting Started

1. **Clone the Repo**:
   ```bash
   git clone https://github.com/rakesh774/DEXPENSE.git
   ```
2. **Open in Android Studio**:
   - Let Gradle sync finish.
   - The project includes an `externalOverride` fix in `build.gradle.kts` to ensure smooth local builds even without a specific release keystore.
3. **Build & Run**:
   - Deploy to any device running Android 8.0 (Oreo) or higher.

---

## 📂 Folder Structure

- `com.example.dexpenses`
  - `MainActivity.java`: Core logic and UI management.
  - `AppDatabase.java`: Room database configuration.
  - `TransactionDao.java` / `EMIDao.java`: Database access objects.
  - `TransactionAdapter.java` / `EMIAdapter.java`: RecyclerView adapters for the dashboard lists.
  - `BalanceWidget.java`: AppWidgetProvider implementation.

---

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.
