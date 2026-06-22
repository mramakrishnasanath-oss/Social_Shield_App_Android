import 'package:flutter/material.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:shared_preferences/shared_preferences.dart';

class NotificationService {
  static final NotificationService _instance = NotificationService._internal();
  factory NotificationService() => _instance;
  NotificationService._internal();

  final FlutterLocalNotificationsPlugin _flutterLocalNotificationsPlugin = FlutterLocalNotificationsPlugin();
  bool _listenerInitialized = false;

  Future<void> initialize() async {
    const AndroidInitializationSettings initializationSettingsAndroid =
        AndroidInitializationSettings('@mipmap/ic_launcher');

    const DarwinInitializationSettings initializationSettingsIOS =
        DarwinInitializationSettings(
      requestSoundPermission: true,
      requestBadgePermission: true,
      requestAlertPermission: true,
    );

    const InitializationSettings initializationSettings = InitializationSettings(
      android: initializationSettingsAndroid,
      iOS: initializationSettingsIOS,
    );

    await _flutterLocalNotificationsPlugin.initialize(
      initializationSettings,
      onDidReceiveNotificationResponse: (details) {
        // Handle notification tap
      },
    );
  }

  // Listen to Firestore scam_alerts in real time (FCM fallback for local testing)
  void initScamAlertsListener() {
    if (_listenerInitialized) return;
    _listenerInitialized = true;

    final DateTime appStartTime = DateTime.now();

    FirebaseFirestore.instance
        .collection('scam_alerts')
        .snapshots()
        .listen((snapshot) async {
          final prefs = await SharedPreferences.getInstance();
          final pushEnabled = prefs.getBool('push_notifications_enabled') ?? true;
          if (!pushEnabled) return;

          for (var change in snapshot.docChanges) {
            if (change.type == DocumentChangeType.added) {
              final data = change.doc.data();
              if (data != null) {
                // Ensure we only alert on NEW detections since app started
                final String? tsStr = data['timestamp'];
                if (tsStr != null) {
                  try {
                    final DateTime timestamp = DateTime.parse(tsStr);
                    if (timestamp.isAfter(appStartTime)) {
                      showSecurityAlert(
                        id: change.doc.id.hashCode,
                        title: data['title'] ?? '⚠ Scam Alert',
                        body: data['body'] ?? 'A suspicious profile has been detected by the community.',
                      );
                    }
                  } catch (_) {}
                }
              }
            }
          }
        }, onError: (e) {
          debugPrint('Error listening to scam alerts: $e');
        });
  }

  Future<void> showSecurityAlert({
    required int id,
    required String title,
    required String body,
  }) async {
    final prefs = await SharedPreferences.getInstance();
    final soundEnabled = prefs.getBool('notification_sound_enabled') ?? true;

    // Use custom sound alert_notification.mp3 from res/raw on Android
    final sound = soundEnabled 
        ? const RawResourceAndroidNotificationSound('alert_notification') 
        : null;

    final AndroidNotificationDetails androidPlatformChannelSpecifics =
        AndroidNotificationDetails(
      'security_alerts_custom_sound_channel',
      'Security Alerts with Sound',
      channelDescription: 'Notifications for high risk security threats with custom sound',
      importance: Importance.max,
      priority: Priority.high,
      color: const Color(0xFFFF3B3B),
      enableLights: true,
      enableVibration: true,
      sound: sound,
      playSound: soundEnabled,
    );
    
    final NotificationDetails platformChannelSpecifics =
        NotificationDetails(android: androidPlatformChannelSpecifics);

    await _flutterLocalNotificationsPlugin.show(
      id,
      title,
      body,
      platformChannelSpecifics,
    );
  }

  Future<void> showThreatWarning({
    required int id,
    required String title,
    required String body,
  }) async {
    const AndroidNotificationDetails androidPlatformChannelSpecifics =
        AndroidNotificationDetails(
      'threat_warnings',
      'Threat Warnings',
      channelDescription: 'Notifications for medium risk threats',
      importance: Importance.high,
      priority: Priority.high,
      color: Color(0xFFFFB800),
    );
    
    const NotificationDetails platformChannelSpecifics =
        NotificationDetails(android: androidPlatformChannelSpecifics);

    await _flutterLocalNotificationsPlugin.show(
      id,
      title,
      body,
      platformChannelSpecifics,
    );
  }

  Future<void> cancelAll() async {
    await _flutterLocalNotificationsPlugin.cancelAll();
  }
}

final notificationServiceProvider = Provider<NotificationService>((ref) {
  return NotificationService();
});
