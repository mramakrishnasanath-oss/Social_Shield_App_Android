class AppConfig {
  AppConfig._();

  // Network Constants
  static const String baseUrl = 'https://socialsheild.onrender.com';
  static const String apiVersion = '/api/v1';

  // API Endpoints
  static const String endpointScanImage = '\$apiVersion/scan/image';
  static const String endpointScanUrl = '\$apiVersion/scan/url';
  static const String endpointScanText = '\$apiVersion/scan/text';
  static const String endpointScanVideo = '\$apiVersion/scan/video';
  static const String endpointScanAudio = '\$apiVersion/scan/audio';
  static const String endpointHistory = '\$apiVersion/history';
  static const String endpointProfile = '\$apiVersion/profile';

  // Dev Token for testing without Firebase Auth
  static const String devToken = 'dev_user_web_token_12345678901234567890';
  
  // App Info
  static const String appName = 'SocialShield';
  static const String appVersion = '1.0.0';
}
