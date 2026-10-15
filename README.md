<div align="center">

<img src="logo.jpg" width="320" height="320" alt="PassTheYear logo">

# PassTheYear

**Your school year, visualized. Less stress, more clarity.**

[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)](https://firebase.google.com/)
[![Material 3](https://img.shields.io/badge/Material%203-Expressive-6750A4?style=for-the-badge&logo=materialdesign&logoColor=white)](https://m3.material.io/)
[![License: AGPL v3](https://img.shields.io/badge/License-AGPLv3-blue?style=for-the-badge)](LICENSE)

</div>

---

## 📖 About

**PassTheYear** is a modern Android app that helps students beat school-related stress and anxiety with a visual, data-driven view of the academic year. Built with **Jetpack Compose**, **Firebase Auth** and **Cloud Firestore**, it follows **Material 3 Expressive** guidelines and supports **Material You** dynamic color.

🌍 **Available in:** English · Italiano · Español

---

## 📲 Download

| Source | Method | Updates |
|---|---|---|
| **[GitHub Releases](https://github.com/madebyanto/passtheyear/releases)** | Manual install (APK) | Manual |
| **[Aura Store](https://store.aurastudioitalia.it)** | One-tap install | Automatic | Simple and fast | Auto updates

👉 Not sure which one to pick? Check out **[WHERE-TO-INSTALL.md](WHERE-TO-INSTALL.md)** for full instructions.

---

## ✨ Features

| | Feature | What it does |
|---|---|---|
| ⏳ | **Smart Countdown** | Shows percentage completed and days remaining, counting only real school days (holidays, weekends and breaks excluded). Includes an interactive circular progress indicator with milestone celebrations at 25%, 50% and 75%. |
| 🗺️ | **Survival Map** | A month-by-month breakdown of the school year, with the vibe, expectations and survival tips for each stretch (November blues, mid-term prep and more). |
| 🛡️ | **De-Stress Zone** | Practical guides on academic recovery, tough exams and stress management, plus a **grade recovery calculator** that shows the exact marks you need to reach a passing average. |
| 📝 | **Journal & Mood Tracker** | A private space to log thoughts, daily moods and personal wins. |
| ⚙️ | **Custom Settings** | Set your own school year start and end dates. |

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Kotlin |
| **UI** | Jetpack Compose + Material 3 Expressive |
| **Architecture** | MVVM + Clean Architecture |
| **Navigation** | Navigation Compose |
| **Auth** | Firebase Authentication (email/password with mandatory email verification) |
| **Database** | Cloud Firestore (real-time sync, data stored under `users/{UID}/..`) |
| **Analytics** | Firebase Analytics |

---

## 🎨 Design

- 🌈 **Dynamic color** that adapts to your wallpaper (Material You, Android 12+)
- 🔤 Expressive typography and fluid motion
- 🔵 Rounded surfaces and Material 3 color roles

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Ladybug or newer
- JDK 17+
- Android SDK 33+
- A Firebase project ([Firebase Console](https://console.firebase.google.com/))

### Self-hosting setup

**1. Clone the repository**

```bash
git clone https://github.com/madebyanto/passtheyear
cd passtheyear
```

**2. Configure Firebase**

1. In the Firebase Console, register an Android app with your package name (e.g. `com.example.schoolcountdown`).
2. Enable **Email/Password** under *Authentication → Sign-in method*.
3. Enable **Cloud Firestore** (test or production mode).
4. Download `google-services.json` and place it in the `app/` folder:

```text
passtheyear/
└── app/
    └── google-services.json
```

**3. Build & run**

Open the project in Android Studio, sync Gradle, then run on an emulator or a physical device with Android 13 (API 33) or higher.

---

## 📄 License

Released under the **AGPLv3** license. See the [LICENSE](LICENSE) file for details.