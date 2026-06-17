# SocialShield Flutter

> AI-Based Smart Mobile Security, Fraud Detection & Optimization Suite

## Setup Instructions

### 1. Install Flutter
- Download Flutter SDK from https://docs.flutter.dev/get-started/install
- Add `flutter/bin` to your PATH

### 2. Install Dependencies
```bash
cd flutter_app
flutter pub get
```

### 3. Firebase Setup
1. Create a project at https://console.firebase.google.com
2. Add Android app with package `com.socialshield.app`
3. Add iOS app with bundle ID `com.socialshield.app`
4. Download `google-services.json` → place in `android/app/`
5. Download `GoogleService-Info.plist` → place in `ios/Runner/`
6. Enable Authentication (Email/Password + Google Sign-In)
7. Enable Firestore Database
8. Run: `flutterfire configure`

### 4. Generate Code
```bash
flutter pub run build_runner build --delete-conflicting-outputs
```

### 5. Run the App
```bash
# Android
flutter run

# iOS
cd ios && pod install && cd ..
flutter run
```

### 6. Backend
The app connects to: https://socialsheild.onrender.com

## Architecture

```
lib/
├── main.dart                    # Entry point
├── app/
│   └── app.dart                 # SocialShieldApp widget
├── core/
│   ├── constants/               # App constants
│   ├── errors/                  # Failure classes
│   ├── network/                 # Dio client
│   ├── theme/                   # App theme & colors
│   └── utils/                   # Validators, extensions
├── models/                      # Data models
├── services/                    # API, Auth, Firestore, Notification
├── repositories/                # Data repositories
├── providers/                   # Riverpod providers
├── routes/                      # GoRouter setup
└── presentation/
    ├── screens/
    │   ├── splash_screen.dart
    │   ├── onboarding_screen.dart
    │   ├── auth/
    │   ├── home/
    │   ├── scan/
    │   ├── analytics/
    │   ├── profile/
    │   └── settings/
    └── widgets/
        ├── common/
        ├── dashboard/
        └── scan/
```

## Key Packages
| Package | Purpose |
|---------|---------|
| flutter_riverpod | State management |
| go_router | Navigation |
| dio | API networking |
| firebase_auth | Authentication |
| cloud_firestore | Database |
| fl_chart | Analytics charts |
| lottie | Animations |
| camera | Camera scanning |
| mobile_scanner | QR scanning |
| shimmer | Loading states |
| flutter_animate | UI animations |

## Backend API Endpoints
| Endpoint | Method | Description |
|----------|--------|-------------|
| /api/v1/scan/image | POST | Scan image for deepfake |
| /api/v1/scan/url | POST | Scan URL for fraud |
| /api/v1/scan/text | POST | Scan text for scam |
| /api/v1/history | GET | Get scan history |
| /api/v1/profile | GET | Get user profile |
