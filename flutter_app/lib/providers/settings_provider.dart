import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:shared_preferences/shared_preferences.dart';

// AI Processing Mode Notifier
class AiProcessingModeNotifier extends StateNotifier<String> {
  AiProcessingModeNotifier() : super('local') {
    _loadPreference();
  }

  static const String _keyMode = 'ai_processing_mode';

  Future<void> _loadPreference() async {
    final prefs = await SharedPreferences.getInstance();
    state = prefs.getString(_keyMode) ?? 'local';
  }

  Future<void> setMode(String mode) async {
    state = mode;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_keyMode, mode);
  }
}

final aiProcessingModeProvider = StateNotifierProvider<AiProcessingModeNotifier, String>((ref) {
  return AiProcessingModeNotifier();
});

// Notification Sound Enabled Notifier
class NotificationSoundEnabledNotifier extends StateNotifier<bool> {
  NotificationSoundEnabledNotifier() : super(true) {
    _loadPreference();
  }

  static const String _keySound = 'notification_sound_enabled';

  Future<void> _loadPreference() async {
    final prefs = await SharedPreferences.getInstance();
    state = prefs.getBool(_keySound) ?? true;
  }

  Future<void> setEnabled(bool enabled) async {
    state = enabled;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool(_keySound, enabled);
  }
}

final notificationSoundEnabledProvider = StateNotifierProvider<NotificationSoundEnabledNotifier, bool>((ref) {
  return NotificationSoundEnabledNotifier();
});

// Notification Sound Volume Notifier
class NotificationSoundVolumeNotifier extends StateNotifier<double> {
  NotificationSoundVolumeNotifier() : super(0.8) {
    _loadPreference();
  }

  static const String _keyVolume = 'notification_sound_volume';

  Future<void> _loadPreference() async {
    final prefs = await SharedPreferences.getInstance();
    state = prefs.getDouble(_keyVolume) ?? 0.8;
  }

  Future<void> setVolume(double volume) async {
    state = volume;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setDouble(_keyVolume, volume);
  }
}

final notificationSoundVolumeProvider = StateNotifierProvider<NotificationSoundVolumeNotifier, double>((ref) {
  return NotificationSoundVolumeNotifier();
});

// Push Notifications Enabled Notifier (Notification Toggle Fix)
class PushNotificationsEnabledNotifier extends StateNotifier<bool> {
  PushNotificationsEnabledNotifier() : super(true) {
    _loadPreference();
  }

  static const String _keyPush = 'push_notifications_enabled';

  Future<void> _loadPreference() async {
    final prefs = await SharedPreferences.getInstance();
    state = prefs.getBool(_keyPush) ?? true;
  }

  Future<void> setEnabled(bool enabled) async {
    state = enabled;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool(_keyPush, enabled);
    
    // Sync with Firebase or notification subscription service if needed
    // e.g. if (enabled) FirebaseMessaging.instance.subscribeToTopic("alerts");
  }
}

final pushNotificationsEnabledProvider = StateNotifierProvider<PushNotificationsEnabledNotifier, bool>((ref) {
  return PushNotificationsEnabledNotifier();
});
