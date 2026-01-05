# 🔍 Lostify - Android Lost & Found App

![Build Status](https://img.shields.io/badge/build-passing-brightgreen) ![Docker](https://img.shields.io/badge/docker-deployed-blue) ![Java](https://img.shields.io/badge/java-17-orange)

**Lostify** is a modern Android application designed to help communities connect lost items with their owners. It allows users to report lost or found items in real-time and communicate securely via an integrated chat system.

## ✨ Key Features
- **🏠 Real-time Feeds:** View lost and found items instantly.
- **💬 Live Chat:** Chat with the finder/owner with 'Seen' status support.
- **🔍 Search:** Filter items easily by category or name.
- **🔐 Secure Auth:** Login/Signup via Firebase Authentication (OTP Supported).
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
| **Containerization** | Docker + Nginx |

---

## 🐳 Docker Deployment (DevOps)
This project is fully containerized to ensure easy distribution. Instead of manual builds, a **Docker Artifact Server** is used to serve the latest stable APK.

### Why this approach?
- **Zero Configuration:** Runs on any machine without installing Android Studio.
- **Stable Release:** Delivers the fully signed APK where **Google OTP/Auth works perfectly**.
- **Self-Hosted:** Mimics a private app store environment.

---

## 🚀 How to Run (Recommended)
You can run the project dashboard immediately using Docker.

### Step 1: Run the Container
Execute the following command in your terminal/PowerShell to start the server:

```bash
docker run -d -p 8080:80 --name lostify_final ghcr.io/hamzatahir17/lostify-app:final