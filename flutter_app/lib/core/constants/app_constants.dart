class AppConstants {
  AppConstants._();

  // Strings
  static const String appTitle = 'SocialShield';
  static const String onboardingTitle1 = 'Detect Deepfakes Instantly';
  static const String onboardingDesc1 = 'Our advanced AI models analyze images and media in seconds to detect manipulation.';
  static const String onboardingTitle2 = 'AI-Powered Fraud Detection';
  static const String onboardingDesc2 = 'Stay protected against phishing links, scam messages, and fraudulent profiles.';
  static const String onboardingTitle3 = 'Your Digital Shield';
  static const String onboardingDesc3 = 'Comprehensive mobile security monitoring and threat analytics in one place.';

  // Storage Keys
  static const String keyOnboardingComplete = 'onboarding_complete';
  static const String keyAuthToken = 'auth_token';
  static const String keyThemeMode = 'theme_mode';

  // Asset Paths
  static const String imageLogo = 'assets/images/logo.png';
  static const String animShield = 'assets/animations/shield.json';
  static const String animScan = 'assets/animations/scan.json';
  static const String animSuccess = 'assets/animations/success.json';
  static const String animWarning = 'assets/animations/warning.json';

  // UI Constants
  static const double borderRadius = 16.0;
  static const double borderRadiusLarge = 24.0;
  static const double paddingLarge = 24.0;
  static const double paddingNormal = 16.0;
  static const double paddingSmall = 8.0;
  
  // Durations
  static const Duration animDurationQuick = Duration(milliseconds: 200);
  static const Duration animDurationNormal = Duration(milliseconds: 300);
  static const Duration animDurationSlow = Duration(milliseconds: 800);
}
