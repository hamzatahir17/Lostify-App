# 🔍 Lostify - Android Lost & Found App

![Build Status](https://img.shields.io/badge/build-passing-brightgreen) ![Docker](https://img.shields.io/badge/docker-supported-blue) ![Java](https://img.shields.io/badge/java-17-orange)

**Lostify** is a modern Android application designed to help communities connect lost items with their owners. It allows users to report lost or found items in real-time and communicate securely via an integrated chat system.

## ✨ Key Features
- **🏠 Real-time Feeds:** View lost and found items instantly.
- **💬 Live Chat:** Chat with the finder/owner with 'Seen' status support.
- **🔍 Search:** Filter items easily by category or name.
- **🔐 Secure Auth:** Login/Signup via Firebase Authentication.
- **🔔 Notifications:** Push notifications for updates.

---

## 🛠️ Tech Stack & Versions
This project is built using modern Android development standards:

| Component | Technology / Version |
|-----------|----------------------|
| **Language** | Java |
| **JDK Version** | OpenJDK 17 |
| **Android SDK** | Target SDK 34 (Android 14) |
| **Minimum SDK** | SDK 24 (Android 7.0) |
| **Build System** | Gradle 8.0+ |
| **Backend** | Firebase Firestore & Auth |
| **Image Loading** | Glide Library |

---

## 🐳 Docker & CI/CD (DevOps)
To ensure a consistent build environment across different machines, this project is fully **Containerized**.

### 1. Docker Integration
- A custom `Dockerfile` is included to automate the setup of Java 17 and Android SDK.
- The build process is isolated, preventing "it works on my machine" issues.

### 2. GitHub Actions (CI/CD)
- **Continuous Integration** is set up. Every push to the `main` branch automatically triggers a workflow to verify the build.
- You can see the **Green Tick ✅** in the Actions tab, confirming the code is error-free.

---

## 🚀 How to Build & Run
You can build this project without installing Android Studio by using Docker.

### Option 1: Using Docker (Recommended)
Run the following commands in your terminal:

**1. Build the Image:**
```bash
docker build -t lostify-app .

### Option 1: Using Docker (Recommended)
Run the following commands in your terminal:

**1. Build the Image:**
`docker build -t lostify-app .`

**2. Run the Container (Generates APK):**
`docker run --name lostify-container lostify-app`

**3. Extract the APK:**
`docker cp lostify-container:/app/app/build/outputs/apk/debug/app-debug.apk ./lostify-final.apk`

### Option 2: Standard Build
1. Clone this repository.
2. Open in **Android Studio**.
3. Sync Gradle and press **Run**.

---

## 📞 Contact
**Developer:** Hamza Tahir
**Email:** hamzatahir111111@gmail.com
