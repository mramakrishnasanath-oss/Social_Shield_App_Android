import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:audioplayers/audioplayers.dart';

import '../../../core/theme/app_colors.dart';
import '../../../providers/settings_provider.dart';
import '../../../providers/theme_provider.dart';
import '../../../providers/auth_provider.dart';
import '../../../services/localization_service.dart';

class SettingsScreen extends ConsumerStatefulWidget {
  const SettingsScreen({super.key});

  @override
  ConsumerState<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends ConsumerState<SettingsScreen> {
  final AudioPlayer _audioPlayer = AudioPlayer();

  @override
  void dispose() {
    _audioPlayer.dispose();
    super.dispose();
  }

  Future<void> _previewSound(double volume) async {
    try {
      await _audioPlayer.setVolume(volume);
      // Plays from assets/sounds/alert_notification.mp3
      await _audioPlayer.play(AssetSource('sounds/alert_notification.mp3'));
    } catch (e) {
      debugPrint('Error playing preview: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    final themeMode = ref.watch(themeProvider);
    final language = ref.watch(languageProvider);
    final aiProcessingMode = ref.watch(aiProcessingModeProvider);
    final soundEnabled = ref.watch(notificationSoundEnabledProvider);
    final soundVolume = ref.watch(notificationSoundVolumeProvider);
    final pushEnabled = ref.watch(pushNotificationsEnabledProvider);

    final isDark = Theme.of(context).brightness == Brightness.dark;

    return Scaffold(
      appBar: AppBar(
        title: Text(Trans.of(context, 'settings')),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => context.go('/home/dashboard'),
        ),
      ),
      body: ListView(
        padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
        children: [
          // Theme Switcher Section
          _buildSectionHeader(context, Trans.of(context, 'theme_mode_title') != 'theme_mode_title' ? Trans.of(context, 'theme_mode_title') : 'Appearance'),
          Card(
            margin: const EdgeInsets.only(bottom: 24),
            child: ListTile(
              leading: Icon(isDark ? Icons.dark_mode_rounded : Icons.light_mode_rounded, color: isDark ? AppColors.darkPrimary : AppColors.lightPrimary),
              title: Text(isDark ? 'Dark Mode' : 'Light Mode'),
              trailing: Switch(
                value: themeMode == ThemeMode.dark,
                activeColor: AppColors.darkPrimary,
                onChanged: (_) => ref.read(themeProvider.notifier).toggleTheme(),
              ),
            ),
          ),

          // Multi-language Support
          _buildSectionHeader(context, Trans.of(context, 'language')),
          Card(
            margin: const EdgeInsets.only(bottom: 24),
            child: ListTile(
              leading: Icon(Icons.translate_rounded, color: isDark ? AppColors.darkPrimary : AppColors.lightPrimary),
              title: Text(Trans.of(context, 'language')),
              trailing: DropdownButton<String>(
                value: language,
                underline: const SizedBox(),
                dropdownColor: isDark ? AppColors.darkCard : Colors.white,
                items: const [
                  DropdownMenuItem(value: 'en', child: Text('English')),
                  DropdownMenuItem(value: 'te', child: Text('తెలుగు')),
                  DropdownMenuItem(value: 'hi', child: Text('हिन्दी')),
                  DropdownMenuItem(value: 'ta', child: Text('தமிழ்')),
                  DropdownMenuItem(value: 'kn', child: Text('ಕನ್ನಡ')),
                  DropdownMenuItem(value: 'ml', child: Text('മലയാളം')),
                ],
                onChanged: (lang) {
                  if (lang != null) {
                    ref.read(languageProvider.notifier).setLanguage(lang);
                  }
                },
              ),
            ),
          ),

          // AI Processing Verification
          _buildSectionHeader(context, Trans.of(context, 'ai_processing')),
          Card(
            margin: const EdgeInsets.only(bottom: 24),
            child: Padding(
              padding: const EdgeInsets.symmetric(vertical: 8.0),
              child: Column(
                children: [
                  RadioListTile<String>(
                    title: Text(Trans.of(context, 'local_processing')),
                    subtitle: const Text('Device-side analysis, works offline'),
                    value: 'local',
                    groupValue: aiProcessingMode,
                    activeColor: isDark ? AppColors.darkPrimary : AppColors.lightPrimary,
                    onChanged: (val) {
                      if (val != null) {
                        ref.read(aiProcessingModeProvider.notifier).setMode(val);
                      }
                    },
                  ),
                  RadioListTile<String>(
                    title: Text(Trans.of(context, 'cloud_processing')),
                    subtitle: const Text('Advanced models, requires internet'),
                    value: 'cloud',
                    groupValue: aiProcessingMode,
                    activeColor: isDark ? AppColors.darkPrimary : AppColors.lightPrimary,
                    onChanged: (val) {
                      if (val != null) {
                        ref.read(aiProcessingModeProvider.notifier).setMode(val);
                      }
                    },
                  ),
                  RadioListTile<String>(
                    title: Text(Trans.of(context, 'hybrid_processing')),
                    subtitle: const Text('Dynamic processing based on network'),
                    value: 'hybrid',
                    groupValue: aiProcessingMode,
                    activeColor: isDark ? AppColors.darkPrimary : AppColors.lightPrimary,
                    onChanged: (val) {
                      if (val != null) {
                        ref.read(aiProcessingModeProvider.notifier).setMode(val);
                      }
                    },
                  ),
                ],
              ),
            ),
          ),

          // Push Notifications (Notification Toggle Fix)
          _buildSectionHeader(context, Trans.of(context, 'notifications')),
          Card(
            margin: const EdgeInsets.only(bottom: 24),
            child: Column(
              children: [
                ListTile(
                  leading: Icon(Icons.notifications_active_rounded, color: isDark ? AppColors.darkPrimary : AppColors.lightPrimary),
                  title: Text(Trans.of(context, 'notifications')),
                  trailing: Switch(
                    value: pushEnabled,
                    activeColor: isDark ? AppColors.darkPrimary : AppColors.lightPrimary,
                    onChanged: (val) {
                      ref.read(pushNotificationsEnabledProvider.notifier).setEnabled(val);
                    },
                  ),
                ),
                if (pushEnabled) ...[
                  const Divider(indent: 16, endIndent: 16),
                  ListTile(
                    leading: Icon(Icons.music_note_rounded, color: isDark ? AppColors.darkPrimary : AppColors.lightPrimary),
                    title: Text(Trans.of(context, 'custom_sound')),
                    trailing: Switch(
                      value: soundEnabled,
                      activeColor: isDark ? AppColors.darkPrimary : AppColors.lightPrimary,
                      onChanged: (val) {
                        ref.read(notificationSoundEnabledProvider.notifier).setEnabled(val);
                      },
                    ),
                  ),
                  if (soundEnabled) ...[
                    Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 8.0),
                      child: Row(
                        children: [
                          Icon(Icons.volume_down_rounded, color: isDark ? AppColors.darkTextSecondary : AppColors.lightTextSecondary),
                          Expanded(
                            child: Slider(
                              value: soundVolume,
                              activeColor: isDark ? AppColors.darkPrimary : AppColors.lightPrimary,
                              onChanged: (val) {
                                ref.read(notificationSoundVolumeProvider.notifier).setVolume(val);
                              },
                            ),
                          ),
                          Icon(Icons.volume_up_rounded, color: isDark ? AppColors.darkTextSecondary : AppColors.lightTextSecondary),
                        ],
                      ),
                    ),
                    ListTile(
                      title: const SizedBox(),
                      trailing: TextButton.icon(
                        icon: const Icon(Icons.play_arrow_rounded),
                        label: Text(Trans.of(context, 'preview')),
                        style: TextButton.styleFrom(
                          foregroundColor: isDark ? AppColors.darkPrimary : AppColors.lightPrimary,
                        ),
                        onPressed: () => _previewSound(soundVolume),
                      ),
                    ),
                  ],
                ],
              ],
            ),
          ),

          // About Section
          Card(
            margin: const EdgeInsets.only(bottom: 32),
            child: Column(
              children: [
                ListTile(
                  leading: const Icon(Icons.info_outline_rounded),
                  title: Text(Trans.of(context, 'about_app')),
                  subtitle: Text(Trans.of(context, 'version')),
                ),
                const Divider(indent: 16, endIndent: 16),
                ListTile(
                  leading: const Icon(Icons.privacy_tip_outlined),
                  title: Text(Trans.of(context, 'privacy_policy')),
                  onTap: () {},
                ),
                const Divider(indent: 16, endIndent: 16),
                ListTile(
                  leading: const Icon(Icons.logout_rounded, color: AppColors.riskHigh),
                  title: Text(Trans.of(context, 'logout'), style: const TextStyle(color: AppColors.riskHigh)),
                  onTap: () async {
                    await ref.read(authNotifierProvider.notifier).signOut();
                    if (context.mounted) {
                      context.go('/auth/login');
                    }
                  },
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildSectionHeader(BuildContext context, String title) {
    final isDark = Theme.of(context).brightness == Brightness.dark;
    return Padding(
      padding: const EdgeInsets.only(left: 4, bottom: 8),
      child: Text(
        title.toUpperCase(),
        style: TextStyle(
          fontSize: 14,
          fontWeight: FontWeight.bold,
          color: isDark ? AppColors.darkPrimary : AppColors.lightPrimary,
          letterSpacing: 1.1,
        ),
      ),
    );
  }
}
